import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import org.jetbrains.annotations.NotNull;

public class StatsCollectingVisitor extends VirtualFileVisitor {
    private static final String KOTLIN_EXT = "kt";

    private ProjectFileIndex index;
    private Stats stats;

    public StatsCollectingVisitor(ProjectFileIndex index) {
        this.stats = new Stats();
        this.index = index;
    }

    @Override
    public boolean visitFile(@NotNull VirtualFile file) {
        if (!file.isDirectory()) {
            processFile(file);
        }

        return super.visitFile(file);
    }

    public Stats getStats() {
        return stats;
    }

    private void processFile(VirtualFile file) {
        if (isTestFile(file) && isKotlinFile(file)) {
            stats.kotlinTestFiles++;
            stats.totalTestFiles++;
        } else if (isTestFile(file)) {
            stats.totalTestFiles++;
        } else if (isKotlinFile(file)) {
            stats.kotlinSourceFiles++;
            stats.totalSourceFiles++;
        } else if (isSourceFile(file)) {
            stats.totalSourceFiles++;
        }
    }

    private boolean isSourceFile(VirtualFile file) {
        return index.isInSource(file);
    }

    private boolean isTestFile(VirtualFile file) {
        return index.isInTestSourceContent(file);
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
