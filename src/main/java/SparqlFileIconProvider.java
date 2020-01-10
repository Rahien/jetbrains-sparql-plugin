import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import icons.SparqlIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SparqlFileIconProvider extends IconProvider {

    public Icon getIcon(@NotNull PsiElement psiElement, int flags) {
        if(!(psiElement instanceof PsiFile)){
            return null;
        }
        PsiFile target = (PsiFile) psiElement;
        if (!target.getName().matches(".*sparql")) {
            return null;
        }else{
            return SparqlIcons.FILE_ICON;
        }
    }
}

