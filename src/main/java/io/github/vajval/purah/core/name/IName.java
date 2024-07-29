package io.github.vajval.purah.core.name;


import static io.github.vajval.purah.core.name.NameUtil.nameByAnnOnClass;

/**
 * 通用的name方法
 * Oh please, as if I'm just supposed to magically know everyone's name through annotations.
 */
public interface IName {

    default String name() {
        return nameByAnnOnClass(this.getClass());
    }

}
