package cc.jinhx.easytool.core;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TreeUtil
 *
 * @author jinhx
 * @since 2022-05-21
 */
@Slf4j
public class TreeUtil {

    /**
     * 将列表转换成树结构
     *
     * @param target 需转换的数据
     * @param getId id
     * @param getParentId 获取父id，父id必须和主键相同类型
     * @param getChildList 获取子集
     * @param setChildList 设置子集
     * @return tree
     */
    public static <T, R> List<T> listToTree(List<T> target, Function<T, R> getId, Function<T, R> getParentId,
                                     Function<T, List<T>> getChildList, BiConsumer<T, List<T>> setChildList) {
        Map<R, T> targetMap = target.stream().collect(Collectors.toMap(getId, t -> t));
        List<T> result = new ArrayList<>();
        target.forEach(tree -> {
            T parent = targetMap.get(getParentId.apply(tree));
            if (Objects.isNull(parent)) {
                result.add(tree);
            } else {
                List<T> ch = getChildList.apply(parent);
                if (Objects.isNull(ch)) {
                    ch = new ArrayList<>();
                }
                ch.add(tree);
                setChildList.accept(parent, ch);
            }
        });
        return result;
    }

}
