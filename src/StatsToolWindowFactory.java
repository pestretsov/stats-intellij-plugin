import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class StatsToolWindowFactory implements ToolWindowFactory {
    private JPanel myToolWindowContent;
    private JProgressBar kotlinSourceProgressBar;
    private JProgressBar kotlinTestProgressBar;
    private JLabel kotlinSourceLabel;
    private JLabel kotlinTestLabel;
    private JButton refreshStatsButton;
    private ToolWindow myToolWindow;

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        refreshStatsButton.addActionListener(e -> StatsToolWindowFactory.this.collectStatsAndUpdateInterface(project));
        myToolWindow = toolWindow;

        this.collectStatsAndUpdateInterface(project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public void collectStatsAndUpdateInterface(Project project) {
        StatsCollectingVisitor.Stats stats = collectStats(project);
        updateInterfaceWithStats(stats);
    }

    private StatsCollectingVisitor.Stats collectStats(Project project) {
        StatsCollectingVisitor visitor = new StatsCollectingVisitor();
        VfsUtilCore.visitChildrenRecursively(project.getBaseDir(), visitor);

        return visitor.getStats();
    }

    private void updateInterfaceWithStats(StatsCollectingVisitor.Stats stats) {
        double srcPercentage = getPercentage(stats.getKotlinSourceFiles(), stats.getTotalSourceFiles());
        kotlinSourceProgressBar.setValue((int) srcPercentage);
        kotlinSourceLabel.setText(formatPercent(srcPercentage));

        double testPercentage = getPercentage(stats.getKotlinTestFiles(), stats.getTotalTestFiles());
        kotlinTestProgressBar.setValue((int) testPercentage);
        kotlinTestLabel.setText(formatPercent(testPercentage));
    }

    private double getPercentage(int a, int b) {
        if (b == 0) return 0;
        return (double) a / b * 100;
    }

    private String formatPercent(double percent) {
        return String.format("%.1f", percent) + "%";
    }
}
