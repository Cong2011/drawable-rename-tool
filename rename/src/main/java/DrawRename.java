import java.io.File;
import java.util.HashMap;
import java.util.Map;

// GBK编码
public class DrawRename implements EtListener {

    private static final String ENV_DIR = ".";

    private UITools ui;
    private Map<File, Map<String, String>> dirNameMap;
    private File currDir;
    private String currName;

    public static void main(String[] args) {
        new DrawRename().deal();
    }

    public void deal() {
        ui = new UITools();
        ui.addTxt("开始执行");
        dirNameMap = this.createNameMap();
        ui.addTxt("扫描到" + dirNameMap.size() + "个文件夹含有drawable或mipmap文件夹");
        if (!dirNameMap.isEmpty()) dealInputStart();
    }

    private void dealInputStart() {
        for (File dir : dirNameMap.keySet()) {
            currDir = dir;
            dealInputDirStart();
            return;
        }
    }

    private void dealInputDirStart() {
        String dirName = currDir.equals(new File("")) ? "当前文件夹" : currDir.getName();
        ui.addTxt("-> 文件夹：" + dirName);
        for (String name : dirNameMap.get(currDir).keySet()) {
            currName = name;
            dealInputName();
            return;
        }
    }

    private void dealInputName() {
        ui.addTxt("请输入新文件名（原名：" + currName + "）");
        ui.addEt(this);
    }

    @Override
    public void onInput(String txt) {
        Map<String, String> nameMap = dirNameMap.get(currDir);
        // 保存输出的新名字
        String newName = txt.trim();
        if (newName.isEmpty()) ui.addTxt("保持原名：" + currName);
        else ui.addTxt("记录:" + currName + " -> " + newName);
        nameMap.put(currName, newName);
        // 寻找下一个
        for (Map.Entry<String, String> entry : nameMap.entrySet()) {
            if (entry.getValue() == null) {
                currName = entry.getKey();
                dealInputName();
                return;
            }
        }
        // 上一文件夹内所有name已设置完毕
        boolean isLastDir = false;
        for (File dir : dirNameMap.keySet()) {
            if (isLastDir) {
                currDir = dir;
                dealInputDirStart();
                return;
            }
            isLastDir = dir == currDir;
        }
        // 所有文件结束，开始改名
        dealStartRename();
    }

    private void dealStartRename() {
        for (Map.Entry<File, Map<String, String>> entry : dirNameMap.entrySet()) {
            File[] drawDirs = entry.getKey().listFiles();
            Map<String, String> nameMap = entry.getValue();
            if (drawDirs != null) for (File dir : drawDirs) {
                if (!isDrawDir(dir)) continue;
                File[] files = dir.listFiles();
                if (files != null) for (File f : files) {
                    if (f.isFile()) dealRename(f, nameMap);
                }
            }
        }
        ui.addTxt("重命名结束，输入确认键结束");
        ui.addEt(new EtListener() {
            @Override
            public void onInput(String txt) {
                System.out.println("输入结束");
                ui.close();
            }
        });
        System.out.println("程序结束");
    }

    // 改名
    private void dealRename(File f, Map<String, String> nameMap) {
        String name = f.getName();
        int i = name.lastIndexOf('.');
        if (i < 0) return;
        String oldName = name.substring(0, i); // 过滤掉后缀
        String newName = nameMap.get(oldName);
        if (newName == null) {
            ui.addTxt("发现未记录文件名：" + f.getAbsolutePath());
            return;
        }
        if (newName.isEmpty()) {
            ui.addTxt("原名：" + oldName);
        } else {
            String suf = name.substring(i);
            boolean b = f.renameTo(new File(f.getParentFile(), newName + suf));
            ui.addTxt((b ? "成功:" : "失败:") + oldName + "->" + newName);
        }
    }

    // -----------------------

    private Map<File, Map<String, String>> createNameMap() {
        HashMap<File, Map<String, String>> map = new HashMap<>();
        scanFileName(map, new File(ENV_DIR));
        return map;
    }

    private void scanFileName(Map<File, Map<String, String>> map, File dir) {
        if (!dir.isDirectory()) return;
        File[] dirs = dir.listFiles();
        Map<String, String> nameMap = new HashMap<>();
        if (dirs != null) for (File drawDir : dirs) { // 遍历子文件夹
            File[] files = drawDir.listFiles();
            if (isDrawDir(drawDir)) {
                if (files == null) continue;
                // 遍历文件夹内的文件
                for (File f : files) {
                    String name = f.getName();
                    int i = name.lastIndexOf('.');
                    if (i < 0) continue;
                    String oldName = name.substring(0, i); // 过滤掉后缀
                    if (!nameMap.containsKey(oldName)) nameMap.put(oldName, null);
                }
            } else {
                scanFileName(map, drawDir);
            }
        }
        if (!nameMap.isEmpty()) map.put(dir, nameMap);
    }

    private boolean isDrawDir(File dir) {
        if (!dir.isDirectory()) return false;
        String name = dir.getName();
        return name.startsWith("drawable") || name.startsWith("mipmap");
    }
}