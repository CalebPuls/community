package com.zyh.community.until;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
        //this.getClass().getClassLoader().getResourceAsStream("");
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
