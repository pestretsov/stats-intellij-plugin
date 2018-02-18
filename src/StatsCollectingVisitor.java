import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import org.jetbrains.annotations.NotNull;

public class StatsCollectingVisitor extends VirtualFileVisitor<SourceFileType> {
    private static final String KOTLIN_EXT = "kt";

    private Stats stats;

    public StatsCollectingVisitor() {
        stats = new Stats();
    }

    @Override
    public boolean visitFile(@NotNull VirtualFile file) {
        if (file.isDirectory()) {
            processDirectory(file);
        } else {
            processFile(file);
        }

        return super.visitFile(file);
    }

    public Stats getStats() {
        return stats;
    }

    private void processDirectory(VirtualFile dir) {
        switch (dir.getName()) {
            case "test":
                setValueForChildren(SourceFileType.TEST);
                break;
            case "main":
                setValueForChildren(SourceFileType.SRC);
                break;
            case "src":
                setValueForChildren(SourceFileType.SRC);
                break;
            default:
                if (getCurrentValue() != null) {
                    setValueForChildren(getCurrentValue());
                } else {
                    setValueForChildren(SourceFileType.OTHER);
                }
                break;
        }
    }

    private void processFile(VirtualFile file) {
        if (isTestFile() && isKotlinFile(file)) {
            stats.kotlinTestFiles++;
            stats.totalTestFiles++;
        } else if (isTestFile()) {
            stats.totalTestFiles++;
        } else if (isKotlinFile(file)) {
            stats.kotlinSourceFiles++;
            stats.totalSourceFiles++;
        } else if (isSourceFile()) {
            stats.totalSourceFiles++;
        }
    }

    private boolean isSourceFile() {
        return getCurrentValue() == SourceFileType.SRC;
    }

    private boolean isTestFile() {
        return getCurrentValue() == SourceFileType.TEST;
    }

    private boolean isKotlinFile(VirtualFile file) {
        return file.getExtension() != null && file.getExtension().equals(KOTLIN_EXT);
    }

    class Stats {
        private int kotlinTestFiles = 0;
        private int kotlinSourceFiles = 0;

        private int totalTestFiles = 0;
        private int totalSourceFiles = 0;

        public int getTotalTestFiles() {
            return totalTestFiles;
        }

        public int getTotalSourceFiles() {
            return totalSourceFiles;
        }

        public int getKotlinSourceFiles() {
            return kotlinSourceFiles;
        }

        public int getKotlinTestFiles() {
            return kotlinTestFiles;
        }
    }
}
