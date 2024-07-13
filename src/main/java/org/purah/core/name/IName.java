package org.purah.core.name;


import static org.purah.core.name.NameUtil.nameByAnnOnClass;

/**
 * Oh please, as if I'm just supposed to magically know everyone's name through annotations.
 */
public interface IName {

    default String name() {
        return nameByAnnOnClass(this.getClass());
    }

}
