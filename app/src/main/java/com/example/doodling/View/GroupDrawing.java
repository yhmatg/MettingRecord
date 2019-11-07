package com.example.doodling.View;

import java.util.List;
import java.util.Objects;

public class GroupDrawing {
    private long groupId;
    private List<Drawing> drawingItems;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupDrawing)) return false;
        GroupDrawing that = (GroupDrawing) o;
        return getGroupId() == that.getGroupId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroupId());
    }

    public GroupDrawing(long groupId, List<Drawing> drawingItems) {
        this.groupId = groupId;
        this.drawingItems = drawingItems;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public List<Drawing> getDrawingItems() {
        return drawingItems;
    }

    public void setDrawingItems(List<Drawing> drawingItems) {
        this.drawingItems = drawingItems;
    }
}
