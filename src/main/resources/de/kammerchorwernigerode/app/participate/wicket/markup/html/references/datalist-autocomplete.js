(function () {
    const DEBOUNCE_MS = 200;

    function escapeAttr(str) {
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }

    function initDatalist(datalist) {
        const url = datalist.dataset.autocompleteUrl;
        if (!url || datalist.dataset.autocompleteInitialized) return;
        datalist.dataset.autocompleteInitialized = 'true';

        const input = document.querySelector('input[list="' + datalist.id + '"]');
        if (!input) return;

        let timer;
        input.addEventListener('input', function () {
            clearTimeout(timer);
            const q = input.value;
            timer = setTimeout(function () {
                if (!q) {
                    datalist.innerHTML = '';
                    return;
                }
                fetch(url + '?q=' + encodeURIComponent(q), {credentials: 'same-origin'})
                    .then(function (r) {
                        return r.json();
                    })
                    .then(function (suggestions) {
                        datalist.innerHTML = suggestions
                            .map(function (s) {
                                return '<option value="' + escapeAttr(s) + '">';
                            })
                            .join('');
                    })
                    .catch(function () {
                    });
            }, DEBOUNCE_MS);
        });
    }

    function initAll() {
        document.querySelectorAll('datalist[data-autocomplete-url]').forEach(initDatalist);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAll);
    } else {
        initAll();
    }
}());
