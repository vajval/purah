package org.purah.core.base;


import static org.purah.core.base.NameUtil.nameByAnnOnClass;

/**
 * Oh please, as if I'm just supposed to magically know everyone's name through annotations.
 */
public interface IName {

    default String name() {
        return nameByAnnOnClass(this.getClass());
    }

}
