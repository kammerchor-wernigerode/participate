package de.vinado.wicket.participate.data.dto;

import de.vinado.wicket.participate.data.permission.Role;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class RoleDTO implements Serializable {

    private Role role;

    private String name;

    private String description;

    public RoleDTO() {
    }

    public RoleDTO(final Role role) {
        this.role = role;
        this.name = role.getName();
        this.description = role.getDescription();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
