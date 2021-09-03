import jxl.*;
import jxl.read.biff.BiffException;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class IncomeTaxPanel extends JFrame implements ActionListener {
    private JSpinner spin_year;
    private JButton confirm;
    private DefaultTableModel tablemodel1, tablemodel2;
    private double a = 0;
    private double b;
    private JLabel filename;
    private JButton read;
    public String[] strings = {"月份", "应发工资", "应缴税", "税后工资"};
    public String[] str1 = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月", "合计", "平均值"};
    protected JFileChooser fchooser1;


    public IncomeTaxPanel() {
        super("月工资及个人所得税");
        this.setBounds(30, 240, 930, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel cmdpanel1 = new JPanel();
        this.getContentPane().add(cmdpanel1, "North");
        JPanel cmdpanel2 = new JPanel();
        this.getContentPane().add(cmdpanel2, "South");
        cmdpanel2.setLayout(new FlowLayout(FlowLayout.LEFT));

        this.spin_year = new JSpinner(new SpinnerNumberModel(2018, 2016, 2020, 1));
        cmdpanel1.add(this.spin_year);
        cmdpanel1.add(new JLabel("年"));

        this.confirm = new JButton("计算");
        cmdpanel1.add(confirm);
        this.confirm.addActionListener(this);

        cmdpanel1.add(this.filename = new JLabel("税率表文件"));
        cmdpanel1.add(this.read = new JButton("读取"));
        this.read.addActionListener(this);

        String[] str = {"级数", "阶级金额（单位：元）", "税率"};
        this.tablemodel1 = new DefaultTableModel(str, 1);
        this.tablemodel1.setRowCount(6);
        this.tablemodel1.setColumnCount(3);
        JTable tax = new JTable(this.tablemodel1);
        cmdpanel2.add(new JScrollPane(tax));                                      //税率表

        this.tablemodel2 = new DefaultTableModel(strings, 1);
        JTable salary = new JTable(this.tablemodel2);
        this.tablemodel2.setRowCount(14);
        this.tablemodel2.setColumnCount(4);
        cmdpanel2.add(new JScrollPane(salary));

        for (int i = 0; i < 14; i++) {
            this.tablemodel2.setValueAt(str1[i], i, 0);
        }

        this.fchooser1 = new JFileChooser();
        this.fchooser1.setCurrentDirectory(new File("C:\\Users\\Harold\\Desktop"));
        this.fchooser1.setFileFilter(new ExtensionFileFilter("表格文件(*.xls)", "xls"));

        this.setVisible(true);
    }


    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == confirm) {
            CalculateTax();
            CalculateSalaryAfterTax();
            TotalSalaryBeforeTax();
            TotalTax();
            TotalSalaryAfterTax();
            AverageSalaryBeforeTax();
            AverageTax();
            AverageSalaryAfterTax();
        }
        if (event.getSource() == read) {
            if (fchooser1.showOpenDialog(this) == 0)
            {
                ReadFrom(fchooser1.getSelectedFile());
            }
        }

    }

    public void ReadFrom(File file) {
        try {
            InputStream in=new FileInputStream(file);
            Workbook workbook = Workbook.getWorkbook(in);
            Sheet sheet=workbook.getSheet(0);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 6; j++) {
                    Cell c1=sheet.getCell(i,j);
                    String cellinto=c1.getContents();
                    this.tablemodel1.setValueAt(cellinto,j,i);
                }
            }
            workbook.close();
            in.close();

        }
        catch (FileNotFoundException fileNotFoundException) {
            JOptionPane.showMessageDialog(this,"文件未找到！");
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(this,"文件读取错误！");
        } catch (BiffException e) {
            JOptionPane.showMessageDialog(this,"表格格式不能解析！");
        }
    }
    public int ExamineNullRowCount()
    {
        int row=0;
        for (int i = 0; i < 12; i++) {
            Object obj=tablemodel2.getValueAt(i,1);
            if(obj!=null)
                row++;
        }
        return row;

    }


    public double TaxResult(double Salary)
    {
       int tax=0;
       int[] TaxStage={3000,9000,13000,10000,20000,25000};
       int[] gap={9000,13000,10000,20000,25000};//gap指每档税率点之间的差
       double[] taxes={0.03,0.1,0.2,0.25,0.3,0.35};
        for (int i = 0; i < 6; i++) {
            Salary=Salary-TaxStage[i];
            if(Salary<=0)
            break;
            if(Salary<=gap[i])
                tax+=Salary*taxes[i];
            else
                tax+=TaxStage[i+1]*taxes[i];
        }
        return tax;
    }


    public void CalculateTax () {
        try {
            int row = ExamineNullRowCount();
            for (int j = 0; j < row; j++) {
                b = Double.parseDouble((String) this.tablemodel2.getValueAt(j, 1));
                a=TaxResult(b);
                this.tablemodel2.setValueAt(String.format("%.2f", a), j, 2);
            }
        }
        catch(NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "请不要输入字符!");
        }
        }


        public void CalculateSalaryAfterTax ()
        {
            int row=ExamineNullRowCount();
                for (int j = 0; j <row ; j++) {
                    a = Double.parseDouble((String) this.tablemodel2.getValueAt(j, 1));
                    b = Double.parseDouble((String) this.tablemodel2.getValueAt(j, 2));
                    double c = a - b;
                    this.tablemodel2.setValueAt(String.format("%.2f", c), j, 3);
                }

            }

        public void TotalSalaryBeforeTax () {
            int row=ExamineNullRowCount();
                double d = 0;
                for (int j = 0; j < row; j++) {
                        d += Double.parseDouble((String) this.tablemodel2.getValueAt(j, 1));

                    }
                    this.tablemodel2.setValueAt(String.format("%.2f", d), 12, 1);
                }



        public void TotalTax ()
            {
                int row=ExamineNullRowCount();
                double d = 0;
                for (int j = 0;j < row; j++) {
                    d += Double.parseDouble((String) this.tablemodel2.getValueAt(j, 2));

                }
                this.tablemodel2.setValueAt(String.format("%.2f", d), 12, 2);
            }


        public void TotalSalaryAfterTax ()
        {
                    double d = 0;
                    double e = Double.parseDouble((String) this.tablemodel2.getValueAt(12, 1));
                    double f = Double.parseDouble((String) this.tablemodel2.getValueAt(12, 2));
                    d = e - f;

                    this.tablemodel2.setValueAt(String.valueOf(d), 12, 3);
                }



        public void AverageSalaryBeforeTax ()
        {
            double d = 0;
            d = Double.parseDouble((String) tablemodel2.getValueAt(12, 1)) / ExamineNullRowCount();
            this.tablemodel2.setValueAt(String.format("%.2f", d), 13, 1);
        }

        public void AverageTax ()
        {
            double d = 0;
            d = Double.parseDouble((String) tablemodel2.getValueAt(12, 2)) / ExamineNullRowCount();
            this.tablemodel2.setValueAt(String.format("%.2f", d), 13, 2);
        }

        public void AverageSalaryAfterTax ()
        {
            double d;
            d = Double.parseDouble((String) tablemodel2.getValueAt(12, 3)) / ExamineNullRowCount();
            this.tablemodel2.setValueAt(String.format("%.2f", d), 13, 3);

        }

    }

    public class IncomeTax {
        public static void main(String[] args) {
            new IncomeTaxPanel();

        }
    }






















