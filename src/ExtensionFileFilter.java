import java.io.File;

public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter
{

    private String description,extension;

    public ExtensionFileFilter(String description,String extension)
    {
        this.description=description;
        this.extension=extension.toLowerCase();
    }
    public boolean accept(File file)
    {
        return file.getName().toLowerCase().endsWith(this.extension);
    }
    public String getDescription()
    {
        return this.description;
    }
}
class XlsFileFilter extends ExtensionFileFilter {

    public XlsFileFilter(String description, String extension) {
        super(description, extension);
    }

    public boolean accept(File f) {

        String nameString = f.getName();
        return nameString.toLowerCase().endsWith(".xls");
    }

    public String getDescription() {

        return "*.xls(表格文件)";
    }

}
