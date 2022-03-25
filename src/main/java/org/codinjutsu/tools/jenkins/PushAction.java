package org.codinjutsu.tools.jenkins;

import com.intellij.dvcs.push.*;
import com.intellij.dvcs.push.ui.PushActionBase;
import com.intellij.dvcs.push.ui.VcsPushUi;
import com.intellij.dvcs.repo.Repository;
import com.intellij.openapi.project.Project;
import org.codinjutsu.tools.jenkins.view.action.RunBuildAction;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PushAction implements PushDialogActionsProvider {
    @NotNull
    @Override
    public List<PushActionBase> getCustomActionsAboveDefault(@NotNull Project project) {
        PushActionBase pushActionBase = new MyPushAction();
        List<PushActionBase> pushActionBases = new ArrayList<>();
        pushActionBases.add(pushActionBase);
        return pushActionBases;
    }

    public class MyPushAction extends PushActionBase {

        MyPushAction() {
            super("Push + Jenkins build");
        }

        @Override
        protected boolean isEnabled(@NotNull VcsPushUi dialog) {
            return true;
        }

        @Override
        protected @Nls @Nullable String getDescription(@NotNull VcsPushUi dialog, boolean enabled) {
            return "Teste";
        }

        @Override
        protected void actionPerformed(@NotNull Project project, @NotNull VcsPushUi dialog) {
            if (dialog.canPush()) {
                dialog.push(false);
            }

            String branch = null;
            for (PushSupport<Repository, PushSource, PushTarget> repositoryPushSourcePushTargetPushSupport : dialog.getSelectedPushSpecs().keySet()) {
                branch = dialog.getSelectedPushSpecs().get(repositoryPushSourcePushTargetPushSupport).stream().findFirst().get().getPushSpec().getSource().getPresentation();
            }
            String finalBranch = branch;
            Thread tr = new Thread(()->{
                try {
                    Thread.sleep(20000);
                    RunBuildAction runBuildAction = new RunBuildAction();
                    runBuildAction.actionPerformed(project, true, finalBranch);
                } catch (InterruptedException e) {
                    //
                }
            });
            tr.start();
        }
    }
}
