import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiInfoDialog extends DialogWrapper {

    Map<String, JTextField> inputMap = new HashMap<>();
    JCheckBox rememberCheckbox;
    SparqlAction sparqlAction;

    public ApiInfoDialog(SparqlAction sparqlAction) {
        super(true); // use current window as parent
        this.sparqlAction = sparqlAction;
        init();
        setTitle("SPARQL Api Info");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));
        dialogPanel.add(createInput("API", false, "http://localhost:8890/"));

        rememberCheckbox = new JCheckBox("Remember until restart");
        rememberCheckbox.setSelected(true);

        dialogPanel.add(rememberCheckbox);

        return dialogPanel;
    }

    private JPanel createInput(String name, boolean password, String defaultValue) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        JLabel label = new JLabel(name, JLabel.LEADING);
        final int columns = 30;
        JTextField textField = null;
        if(password){
            textField = new JPasswordField(defaultValue, columns);
        }else {
            textField = new JTextField(defaultValue, columns);
        }
        panel.add(label);
        panel.add(textField);

        inputMap.put(name, textField);
        return panel;
    }

    @Override
    protected Action getOKAction() {
        return new ExecuteAction();
    }



    private class ExecuteAction extends DialogWrapperAction {
        protected ExecuteAction() {
            super("Ok");
            putValue(Action.NAME, "Ok");
        }

        @Override
        protected void doAction(ActionEvent e) {
            close(0);

            try{
                SPARQLApi.login(
                        inputMap.get("API").getText(),
                        rememberCheckbox.isSelected());
                sparqlAction.executeWithMessages();

            }catch (Exception exception){
                System.out.println("Failed: "+exception);
            }
        }
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        ArrayList<String> texts = new ArrayList<>();
        texts.add(this.inputMap.get("API").getText());
        texts.add(this.inputMap.get("User").getText());
        texts.add(this.inputMap.get("Password").getText());

        for(String text : texts){
            if(text == null || text.isEmpty()){
                return new ValidationInfo("We'll need a little more info to contact the SPARQL Endpoint");
            }
        }
        return null;
    }
}