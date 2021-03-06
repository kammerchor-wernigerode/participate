#!/bin/bash

# Preamble: This script unfolds its full power if the git tags have the following format: vX.X.X

tags=()

# Splits a tag number e.g. v3.1.0 into an array of ( 3 1 0 ). The given tag must start with a lowercase "v" otherwise
# the validation fails. The array will be stored in the TAGS variable.
function split() {
  local tag=$1

  if [[ ${tag} =~ ^v([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
    tag=$(echo ${tag} | cut -d "v" -f 2) # Removes "v" from the tag. Eg v3.1.0 to 3.1.0
    IFS='.'                              # Overrides the internal file separator
    tags=(${tag})                        # Turns the tag into an array
    IFS=${OIFS}                          # Restores the original separator
  else
    echo "'$tag' doesn't seem to be an acceptable tag"
    exit 1
  fi

  return 0
}

# Takes any string and tags the :latest Docker image. The image will be pushed to the Docker registry afterwards.
function tag_and_push() {
  local tag=$1

  # Omit the :latest tag
  [[ ${tag} != "latest" ]] && docker tag ${DOCKER_REPO_SLUG}:latest ${DOCKER_REPO_SLUG}:${tag}
  docker push ${DOCKER_REPO_SLUG}:${tag}

  return 0
}

echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin || (echo "Unable to login"; exit 1)
docker build -t ${DOCKER_REPO_SLUG}:latest .

# Matching branch name and git tag implies a new release. The Docker image will be tagged with :latest and convenient
# version numbers. E.g. v0.0.1=>0.0.1  v0.1.0=>0.1,0.1.0  v0.1.2=>0.1,0.1.2  v1.0.0=>1,1.0,1.0.0  v1.0.3=>1,1.0,1.0.3
# v1.2.0=>1,1.2,1.2.0  v1.2.3=>1,1.2,1.2.3
if [[ ${TRAVIS_TAG} == ${TRAVIS_BRANCH} ]]; then
  split ${TRAVIS_TAG}

  major=${tags[0]}
  minor=${major}.${tags[1]}
  patch=${minor}.${tags[2]}

  tag_and_push "latest" # The git tag must be valid otherwise the script does not push the :latest.
  [[ ${tags[0]} -gt 0 ]] && tag_and_push ${major}
  [[ ${tags[0]} -gt 0 || ${tags[1]} -gt 0 ]] && tag_and_push ${minor}
  [[ ${tags[0]} -gt 0 || ${tags[1]} -gt 0 && ${tags[2]} -gt 0 ]] && tag_and_push ${patch}

  # Call webhook for automated deployment
  if [[ -n ${WEBHOOK_TARGET_ADDRESS} ]]; then
    curl -X POST \
      --write-out "%{http_code}\n" \
      --silent \
      --show-error \
      --connect-timeout 5 \
      --output /dev/null \
      ${WEBHOOK_TARGET_ADDRESS}
  fi
fi

exit 0
