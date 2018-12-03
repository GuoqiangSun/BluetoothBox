package com.bazooka.bluetoothbox.bean.event;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/29
 *         作用：闪法发送进度事件
 */

public class FlashSendProgress {

    private int progress;
    private int total;
    private boolean isCancel;

    public FlashSendProgress(int progress, int total, boolean isCancel) {
        this.progress = progress;
        this.total = total;
        this.isCancel = isCancel;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    /**
     * 发送完成事件
     */
    public static class FlashSendFinish {
        private int succeedNum;
        private int failedNum;

        public FlashSendFinish(int succeedNum, int failedNum) {
            this.succeedNum = succeedNum;
            this.failedNum = failedNum;
        }

        public int getSucceedNum() {
            return succeedNum;
        }

        public void setSucceedNum(int succeedNum) {
            this.succeedNum = succeedNum;
        }

        public int getFailedNum() {
            return failedNum;
        }

        public void setFailedNum(int failedNum) {
            this.failedNum = failedNum;
        }
    }
}
