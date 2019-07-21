package app.util;

import app.pojo.EsPage;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.function.BinaryOperator;

/**
 * 词频统计工具类
 *
 * @author faith.huan 2019-07-21 10:40
 */
@Slf4j
public class WordCountUtil {

    /**
     * 统计词频
     *
     * @param baseDir        文件所在路径
     * @param resultFileName 统计结果文件名
     */
    public static void wordCount(String baseDir, String resultFileName) {
        try {
            TreeMap<String, Integer> resultMap = new TreeMap<>();
            List<String> subFiles = getSubFiles(new File(baseDir));
            for (String subFile : subFiles) {
                Map<String, Integer> map = count(subFile);
                addToTreeMap(resultMap, map);
            }
            saveToFile(resultMap, resultFileName);
        } catch (Exception e) {
            log.error("统计词频发生异常", e);
        }
    }

    /**
     * 统计单个文件词频
     *
     * @param fileName 文件名
     */
    private static Map<String, Integer> count(String fileName) {
        try {
            String string = IOUtils.toString(new FileInputStream(fileName));
            EsPage esPage = JSON.parseObject(string, EsPage.class);
            log.info("file:{},esPage:{}", fileName, esPage.getTitle());
            if (StringUtils.isAnyBlank(esPage.getContent(), esPage.getTitle())) {
                return Collections.emptyMap();
            }
            String content = esPage.getContent() + " " + esPage.getTitle();
            content = content.replaceAll("[@`;“”®\":.?!()$<%*#}{\\[\\]\\d]+", "").toLowerCase();

            Optional<HashMap<String, Integer>> reduce = Arrays.stream(content.split(" "))
                    .flatMap(s -> Arrays.stream(s.split("[’'/…—_+\n\\-]+")))
                    .filter(s -> StringUtils.isNotBlank(s) && !StringUtils.containsAny(s, ",") && s.length() > 4)
                    .map(s -> {
                        HashMap<String, Integer> map = new HashMap<String, Integer>(4) {{
                            put(s, 1);
                        }};
                        return map;
                    }).reduce(new BinaryOperator<HashMap<String, Integer>>() {
                        @Override
                        public HashMap<String, Integer> apply(HashMap<String, Integer> map1, HashMap<String, Integer> map2) {

                            map2.keySet().forEach(key -> {
                                if (map1.containsKey(key)) {
                                    map1.put(key, map1.get(key) + map2.get(key));
                                } else {
                                    map1.put(key, map2.get(key));
                                }
                            });
                            return map1;
                        }
                    });

            if (reduce.isPresent()) {
                return reduce.get();
            } else {
                return Collections.emptyMap();
            }

        } catch (IOException e) {
            log.error("统计文件{}词频发生异常", fileName, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 将统计结果写入文件
     * 会写两个文件
     * ${resultFileName}SortedByKey.txt:存储按字母排序的结果
     * ${resultFileName}SortedByValue.txt:存储按词频逆序排序的结果
     *
     * @param treeMap        统计结果
     * @param resultFileName 文件名
     */
    private static void saveToFile(TreeMap<String, Integer> treeMap, String resultFileName) {

        try {
            StringBuilder stringBuilder = new StringBuilder();
            treeMap.forEach((key, val) -> {
                stringBuilder.append(key).append(":")
                        .append(val).append("\n");
            });
            IOUtils.write(stringBuilder.toString(), new FileOutputStream(resultFileName + "SortedByKey.txt"));
            // 排序
            List<Map.Entry<String, Integer>> resultList = sort(treeMap);
            StringBuilder stringBuilderSorted = new StringBuilder();
            resultList.forEach(e -> {
                stringBuilderSorted.append(e.getKey()).append(":")
                        .append(e.getValue()).append("\n");
            });
            IOUtils.write(stringBuilderSorted.toString(), new FileOutputStream(resultFileName + "SortedByValue.txt"));
        } catch (IOException e) {
            System.out.println("写入发生异常");
        }

    }

    /**
     * 按词频逆序排列统计结果
     */
    private static List<Map.Entry<String, Integer>> sort(Map<String, Integer> resultMap) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(resultMap.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return list;
    }

    /**
     * 将单个文件结果汇总到合计中
     *
     * @param resultMap 合计map
     * @param map       单个文件map
     */
    private static void addToTreeMap(Map<String, Integer> resultMap, Map<String, Integer> map) {
        map.keySet().forEach(key -> {
            if (resultMap.containsKey(key)) {
                resultMap.put(key, resultMap.get(key) + map.get(key));
            } else {
                resultMap.put(key, map.get(key));
            }
        });
    }

    /**
     * 取子文件列表
     *
     * @param f 父文件
     * @return 子文件列表
     */
    private static List<String> getSubFiles(File f) {
        List<String> subFiles = new ArrayList<>();
        if (f.isDirectory()) {
            File[] fs = f.listFiles();
            if (fs != null) {
                for (File file : fs) {
                    if (file.isDirectory()) {
                        subFiles.addAll(getSubFiles(file));
                    } else {
                        subFiles.add(file.getAbsolutePath());
                    }
                }
            }
        } else {
            subFiles.add(f.getAbsolutePath());
        }
        return subFiles;
    }

}
