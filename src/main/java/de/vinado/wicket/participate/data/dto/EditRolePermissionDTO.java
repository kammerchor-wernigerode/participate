package de.vinado.wicket.participate.data.dto;

import de.vinado.wicket.participate.data.permission.Role;
import de.vinado.wicket.participate.data.permission.RoleToPermission;

import java.io.Serializable;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EditRolePermissionDTO implements Serializable {

    private Role role;

    private List<RoleToPermission> permissions;

    public EditRolePermissionDTO(final Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public List<RoleToPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(final List<RoleToPermission> permissions) {
        this.permissions = permissions;
    }
}
