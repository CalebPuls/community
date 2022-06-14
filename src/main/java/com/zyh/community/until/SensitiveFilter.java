package com.zyh.community.until;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @Description
 * @create 2022-06-01 20:23
 */
@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT = "***";
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init(){

        try(
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader  = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyWord;
            while ((keyWord = reader.readLine()) != null){
                this.addKeyWords(keyWord);
            }
        } catch (Exception e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }
    }

    //将敏感词添加到前缀树
    public void addKeyWords(String keyWords){
        TrieNode trieNode = root;
        for (int i=0;i<keyWords.length();++i){
            char c = keyWords.charAt(i);
            TrieNode subNode = trieNode.getSubNodes(c);
            if (subNode == null){
                subNode = new TrieNode();
                trieNode.addSubNode(c,subNode);
            }
            trieNode = subNode;
            if (i == keyWords.length()-1){
                trieNode.setKeyWord(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return 过滤后文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text))return null;
        //指针1
        TrieNode point1 = root;
        int begin = 0;
        int end = 0;
        //结果
        StringBuilder stringBuilder = new StringBuilder();
        while (begin < text.length()){
            char c = text.charAt(end);
            //如果是特殊符号跳过
            if(isSymbol(c)){
                if (point1 == root){
                    stringBuilder.append(c);
                    ++begin;
                }
                ++end;
                continue;
            }
            //指向下一个节点
            point1 = point1.getSubNodes(c);
            //若节点为空，说明begin指的不是敏感词
            if (point1 == null){
                stringBuilder.append(text.charAt(begin));
                end = ++begin;
                point1 = root;
            }else if (point1.isKeyWord()){//若是有标识节点节点，说明begin-end是敏感词
                stringBuilder.append(REPLACEMENT);
                begin = ++end;
                point1 = root;
            }else {
                ++end;
            }
        }
        return stringBuilder.toString();
    }

    //判断是否为符号
    private  boolean isSymbol(Character character){
        return !CharUtils.isAsciiAlphanumeric(character)
                && (character<0x2E80||character>0x9FFF);//这里0x2E80-0x9FFF表示为东亚文字
    }
    //前缀树
    private class TrieNode{
        //关键词结束标识
        private boolean isKeyWord = false;
        //子节点key是下级字符，value是下级节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWord() {
            return isKeyWord;
        }

        public void setKeyWord(boolean keyWord) {
            isKeyWord = keyWord;
        }

        public void addSubNode(Character c,TrieNode t){
            subNodes.put(c,t);
        }
        public TrieNode getSubNodes(Character c){
            return subNodes.get(c);
        }
    }

}
