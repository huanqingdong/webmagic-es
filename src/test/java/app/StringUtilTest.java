package app;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * 注释 $ 匹配输入字符串结尾的位置。如果设置了 RegExp 对象的 Multiline 属性，那么 $ 还匹配 \n 或 \r 前面的位置。若要匹配 $ 字符本身，请使用 \$。
 * <p>
 * ( ) 标记子表达式的开始和结束。可以捕获子表达式以供以后使用。若要匹配这两个字符，请使用 \( 和 \)。
 * <p>
 * * 零次或多次匹配前面的字符或子表达式。若要匹配 * 字符，请使用 \*。
 * <p>
 * + 一次或多次匹配前面的字符或子表达式。若要匹配 + 字符，请使用 \+。
 * <p>
 * . 匹配除换行符 \n 之外的任何单个字符。若要匹配 .，请使用 \。 [ ] 标记中括号表达式的开始。若要匹配这些字符，请使用 \[ 和 \]。
 * <p>
 * ? 零次或一次匹配前面的字符或子表达式，或指示“非贪心”限定符。若要匹配 ? 字符，请使用 \?。
 * <p>
 * \ 将下一字符标记为特殊字符、文本、反向引用或八进制转义符。例如，字符 n 匹配字符 n。\n 匹配换行符。序列 \\ 匹配 \，序列 \( 匹配 (。
 * <p>
 * / 表示文本正则表达式的开始或结束。若要匹配 / 字符，请使用 \/。
 * <p>
 * ^ 匹配输入字符串开始处的位置，但在中括号表达式中使用的情况除外，在那种情况下它对字符集求反。若要匹配 ^ 字符本身，请使用 \^。
 * <p>
 * { } 标记限定符表达式的开始。若要匹配这些字符，请使用 \{ 和 \}。
 * <p>
 * | 指出在两个项之间进行选择。若要匹配 | ，请使用 \|
 *
 * @author faith.huan 2019-07-21 16:12
 */
@Slf4j
public class StringUtilTest {

    String txt = "h212e]ll\"o , i am Cu[stom-routing_patterns@@_can be implemented `by` specifying a custom  value ";

    String txt1 = "##2#";

    @Test
    public void split() {
        String[] res = StringUtils.split(txt);
        log.info("split by whitespace:{}", JSON.toJSONString(res, true));
        res = StringUtils.split(txt1, "#");
        log.info("split by whitespace:{}", JSON.toJSONString(res, true));

        res = txt.split("[ ,_\\-]+");
        log.info("split by reg:{}", JSON.toJSONString(res, true));

    }

    @Test
    public void replace() {
        String res = txt.replaceAll("[\\d@`;“\"\\[\\]:.?()$®<”%*#}{]+", "");
        //String res = txt.replaceAll("[]:.?()/\"1234567890;“$®<”%*#}{]+", "");
        log.info("res:{}", res);
    }
}
