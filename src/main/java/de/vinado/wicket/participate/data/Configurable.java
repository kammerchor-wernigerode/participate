package de.vinado.wicket.participate.data;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public interface Configurable extends Identifiable {

    @Override
    Long getId();

    String getName();

    void setName(String name);

    String getIdentifier();

    boolean isActive();

    void setActive(boolean active);

    boolean isDefault();

    Long getSortOrder();

    void setSortOrder(Long sortOrder);
}
