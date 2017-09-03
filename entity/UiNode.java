package com.repackage.entity;

import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by Shinelon on 2017/5/25.
 */
public class UiNode {
    private boolean hasVisited = false;

    private int depth;

    private String bounds;
    private String id;

    private String preWindowID;
    private String windowID;

    private String windowsIDTransition;

    private String action;

    private String info;

    private Integer winIndex;

    private String winIndexTransition;

    private UiNode leftNode = null;

    private WebElement element;

    private List<UiNode> rightNode = null;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getWindowID() {
        return windowID;
    }

    public void setWindowID(String windowID) {
        this.windowID = windowID;
    }

    public String getPreWindowID() {
        return preWindowID;
    }

    public void setPreWindowID(String preWindowID) {
        this.preWindowID = preWindowID;
    }

    public String getWindowsIDTransition() {
        return windowsIDTransition;
    }

    public void setWindowsIDTransition(String windowsIDTransition) {
        this.windowsIDTransition = windowsIDTransition;
    }


    public String getBounds(){
        return bounds;
    }

    public void setBounds(String bounds){
        this.bounds=bounds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UiNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(UiNode leftNode) {
        this.leftNode = leftNode;
    }

    public List<UiNode> getRightNode() {
        return rightNode;
    }

    public void setRightNode(List<UiNode> rightNode) {
        this.rightNode = rightNode;
    }

    public boolean isHasVisited() {
        return hasVisited;
    }

    public void setHasVisited(boolean hasVisited) {
        this.hasVisited = hasVisited;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public WebElement getElement() {
        return element;
    }

    public void setElement(WebElement element) {
        this.element = element;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getWinIndex(){return winIndex;}

    public void setWinIndex(Integer winIndex){this.winIndex=winIndex;}

    public String getWinIndexTransition(){return winIndexTransition;}

    public void setWinIndexTransition(String winIndexTransition) {
        this.winIndexTransition = winIndexTransition;
    }
}
