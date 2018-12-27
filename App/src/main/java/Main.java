import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import java.net.*;
import java.io.*;

import java.nio.file.*;

import ru.ifmo.ct.khalansky.coursework.client.ClientFacade;

public class Main {

    private static class Measurement {
        long meanClient;
        long meanInteraction;
        long meanProcessing;

        Measurement(long meanClient, long meanInteraction, long meanProcessing)
        {
            this.meanClient = meanClient;
            this.meanInteraction = meanInteraction;
            this.meanProcessing = meanProcessing;
        }
    }

    private static void draw(
        double[] xs, double[] ys, String xLabel, String series, String plotName)
    {
        JFrame f = new JFrame(plotName);

        DefaultXYDataset ds = new DefaultXYDataset();

        double[][] data = { xs, ys };
        ds.addSeries(series, data);

        JFreeChart chart = ChartFactory.createXYLineChart(null,
                xLabel, "ms", ds, PlotOrientation.VERTICAL, false, false,
                false);

        ChartPanel cp = new ChartPanel(chart);
        f.getContentPane().add(cp);
        f.setSize(800, 600);
        f.setVisible(true);
    }

    private static void showPlots(
        List<Integer> vals, List<Measurement> msms, String xLabel, String series)
    {
        double xs[] = new double[vals.size()];
        double clients[] = new double[vals.size()];
        double interactions[] = new double[vals.size()];
        double processings[] = new double[vals.size()];

        for (int i = 0; i < vals.size(); ++i) {
            xs[i] = vals.get(i);
            clients[i]      = msms.get(i).meanClient;
            interactions[i] = msms.get(i).meanInteraction;
            processings[i]  = msms.get(i).meanProcessing;
        }

        draw(xs, clients, xLabel, series, "Clients");
        draw(xs, interactions, xLabel, series, "Interactions");
        draw(xs, processings, xLabel, series, "Processings");
        
    }

    private static Measurement runClients(
        InetAddress address, short mport, short port,
        byte serverId, int x, int n, int m, int d) throws IOException
    {
        try (Socket socket = new Socket(address, mport);
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream())
        ) {
            os.writeByte(1);
            os.writeByte(serverId);
            is.readByte();
        }

        ClientFacade.runClients(address, port, n, m, d * 1000, x);

        try (Socket socket = new Socket(address, mport);
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream())
        ) {
            os.writeByte(2);
        }

        try (Socket socket = new Socket(address, mport);
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream())
        ) {
            os.writeByte(3);
            long meanClient = is.readLong() / x - d / 1000;
            long meanInteraction = is.readLong();
            long meanProcessing = is.readLong();
            return new Measurement(meanClient, meanInteraction, meanProcessing);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("JAVA GUI APPLICATION by Димас dimas96 Халанский");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ButtonGroup sbg = new ButtonGroup();

        JRadioButton server1 = new JRadioButton("Separate threads", false);
        server1.setBounds(5, 20 * 1 - 15, 170, 20);
        server1.setBackground(Color.yellow);
        f.add(server1);
        sbg.add(server1);
        JRadioButton server2 = new JRadioButton("Shared pool", false);
        server2.setBounds(5, 20 * 2 - 15, 170, 20);
        server2.setBackground(Color.magenta);
        f.add(server2);
        sbg.add(server2);
        JRadioButton server3 = new JRadioButton("Nonblocking", false);
        server3.setBounds(5, 20 * 3 - 15, 170, 20);
        server3.setBackground(Color.gray);
        f.add(server3);
        sbg.add(server3);
        JRadioButton server4 = new JRadioButton("Asynchronous", false);
        server4.setBounds(5, 20 * 4 - 15, 170, 20);
        server4.setBackground(Color.blue);
        // f.add(server4);
        sbg.add(server4);

        JTextField address = new JTextField("127.0.0.1");
        address.setBounds(180, 5, 120, 20);
        f.add(address);
        JTextField mport = new JTextField("3083");
        mport.setBounds(305, 5, 40, 20);
        f.add(mport);
        JTextField port = new JTextField("3084");
        port.setBounds(350, 5, 40, 20);
        f.add(port);

        SpinnerNumberModel xSpinner = new SpinnerNumberModel(50, 1, null, 1);
        JLabel numberOfQueriesL = new JLabel("X:");
        numberOfQueriesL.setBounds(180, 95, 20, 20);
        f.add(numberOfQueriesL);
        JSpinner numberOfQueries = new JSpinner(xSpinner);
        numberOfQueries.setBounds(205, 95, 60, 20);
        f.add(numberOfQueries);

        ButtonGroup qbg = new ButtonGroup();

        SpinnerNumberModel nSpinner = new SpinnerNumberModel(5000, 1, null, 1);
        SpinnerNumberModel nMinSpinner = new SpinnerNumberModel(1000, 1, null, 1);
        SpinnerNumberModel nStepSpinner = new SpinnerNumberModel(500, 1, null, 1);
        SpinnerNumberModel mSpinner = new SpinnerNumberModel(20, 1, null, 1);
        SpinnerNumberModel mMinSpinner = new SpinnerNumberModel(3, 1, null, 1);
        SpinnerNumberModel mStepSpinner = new SpinnerNumberModel(3, 1, null, 1);
        SpinnerNumberModel dSpinner = new SpinnerNumberModel(10000, 1, null, 1);
        SpinnerNumberModel dMinSpinner = new SpinnerNumberModel(1000, 1, null, 1);
        SpinnerNumberModel dStepSpinner = new SpinnerNumberModel(100, 1, null, 1);

        JSpinner nMin = new JSpinner(nMinSpinner);
        nMin.setBounds(220, 10 + 20 * 1, 60, 20);
        JSpinner nMax = new JSpinner(nSpinner);
        nMax.setBounds(285, 10 + 20 * 1, 60, 20);
        JSpinner nStep = new JSpinner(nStepSpinner);
        nStep.setBounds(350, 10 + 20 * 1, 45, 20);

        JSpinner mMin = new JSpinner(mMinSpinner);
        mMin.setBounds(220, 10 + 20 * 2, 60, 20);
        JSpinner mMax = new JSpinner(mSpinner);
        mMax.setBounds(285, 10 + 20 * 2, 60, 20);
        JSpinner mStep = new JSpinner(mStepSpinner);
        mStep.setBounds(350, 10 + 20 * 2, 45, 20);

        JSpinner dMin = new JSpinner(dMinSpinner);
        dMin.setBounds(220, 10 + 20 * 3, 60, 20);
        JSpinner dMax = new JSpinner(dSpinner);
        dMax.setBounds(285, 10 + 20 * 3, 60, 20);
        JSpinner dStep = new JSpinner(dStepSpinner);
        dStep.setBounds(350, 10 + 20 * 3, 45, 20);

        JRadioButton change1 = new JRadioButton("N", false);
        JRadioButton change2 = new JRadioButton("M", false);
        JRadioButton change3 = new JRadioButton("∆", false);

        ActionListener variableSelector = (e) -> {
            f.remove(dMin);
            f.remove(dMax);
            f.remove(dStep);
            f.remove(mMin);
            f.remove(mMax);
            f.remove(mStep);
            f.remove(nMin);
            f.remove(nMax);
            f.remove(nStep);
            if (change1.isSelected()) {
                f.add(mMax);
                f.add(dMax);
                f.add(nMin);
                f.add(nMax);
                f.add(nStep);
            } else if (change2.isSelected()) {
                f.add(nMax);
                f.add(dMax);
                f.add(mMin);
                f.add(mMax);
                f.add(mStep);
            } else if (change3.isSelected()) {
                f.add(nMax);
                f.add(mMax);
                f.add(dMin);
                f.add(dMax);
                f.add(dStep);
            }
            f.repaint();
            f.validate();
        };

        change1.addActionListener(variableSelector);
        change1.setBounds(180, 10 + 20 * 1, 40, 20);
        f.add(change1);
        qbg.add(change1);
        change2.addActionListener(variableSelector);
        change2.setBounds(180, 10 + 20 * 2, 40, 20);
        f.add(change2);
        qbg.add(change2);
        change3.addActionListener(variableSelector);
        change3.setBounds(180, 10 + 20 * 3, 40, 20);
        f.add(change3);
        qbg.add(change3);

        JButton runButton = new JButton("Run");

        ActionListener needToRun = (e) -> {

            int x = xSpinner.getNumber().intValue();
            int nV = nSpinner.getNumber().intValue();
            int nMinV = nMinSpinner.getNumber().intValue();
            int nSV = nStepSpinner.getNumber().intValue();
            int mV = mSpinner.getNumber().intValue();
            int mMinV = mMinSpinner.getNumber().intValue();
            int mSV = mStepSpinner.getNumber().intValue();
            int dV = dSpinner.getNumber().intValue();
            int dMinV = dMinSpinner.getNumber().intValue();
            int dSV = dStepSpinner.getNumber().intValue();
 
            byte servV;
            String seriesName;
            if (server1.isSelected()) {
                servV = 1;
                seriesName = "Separate threads";
            } else if (server2.isSelected()) {
                servV = 2;
                seriesName = "Thread pool";
            } else if (server3.isSelected()) {
                servV = 3;
                seriesName = "Nonblocking";
            } else if (server4.isSelected()) {
                servV = 4;
                seriesName = "Asynchronous";
            } else {
                JOptionPane.showMessageDialog(
                    f,
                    "No server type selected",
                    "ЕГГОГ",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int changing;

            if (change1.isSelected()) {
                changing = 1;
            } else if (change2.isSelected()) {
                changing = 2;
            } else if (change3.isSelected()) {
                changing = 3;
            } else {
                JOptionPane.showMessageDialog(
                    f,
                    "No variable type selected",
                    "ЕГГОГ",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            InetAddress host;
            try {
                host = InetAddress.getByName(address.getText());
            } catch (UnknownHostException ex) {
                JOptionPane.showMessageDialog(
                    f,
                    "Unknown host " + address.getText(),
                    "ЕГГОГ",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            short portV;
            try {
                portV = Short.parseShort(port.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    f,
                    "Invalid port: " + port.getText(),
                    "ЕГГОГ",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            short mportV;
            try {
                mportV = Short.parseShort(mport.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    f,
                    "Invalid port: " + mport.getText(),
                    "ЕГГОГ",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            runButton.setEnabled(false);
            SwingUtilities.invokeLater(() ->
            {
                List<Integer> vals = new ArrayList<Integer>();
                List<Measurement> msms = new ArrayList<Measurement>();
                String xLbl = "unknown";
                try {
                    switch (changing) {
                        case 1: {
                            xLbl = "n";
                            int m = mV;
                            int d = dV;
                            for (int n = nMinV; n <= nV; n += nSV) {
                                msms.add(runClients(host, mportV, portV, servV, x, n, m, d));
                                vals.add(n);
                            }
                            break;
                        }
                        case 2: {
                            xLbl = "m";
                            int n = nV;
                            int d = dV;
                            for (int m = mMinV; m <= mV; m += mSV) {
                                msms.add(runClients(host, mportV, portV, servV, x, n, m, d));
                                vals.add(m);
                            }
                            break;
                        }
                        case 3: {
                            xLbl = "d";
                            int n = nV;
                            int m = mV;
                            for (int d = dMinV; d <= dV; d += dSV) {
                                msms.add(runClients(host, mportV, portV, servV, x, n, m, d));
                                vals.add(d);
                            }
                            break;
                        }
                    }
                } catch (SocketException ex) {
                    JOptionPane.showMessageDialog(
                        f,
                        "Connect exception: " + ex.toString(),
                        "ЕГГОГ",
                        JOptionPane.ERROR_MESSAGE);
                    runButton.setEnabled(true);
                    return;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                showPlots(vals, msms, xLbl, seriesName);
                Path resDir = Paths.get((new Date()).toString());
                try {
                    Files.createDirectory(resDir);
                    try (OutputStream os = Files.newOutputStream(
                        resDir.resolve("description.txt"));
                        PrintWriter writer = new PrintWriter(os))
                    {
                        writer.println(seriesName);
                        switch (changing) {
                            case 1: {
                                writer.print("changing n by [");
                                writer.print(nMinV);
                                writer.print(", ");
                                writer.print(nMinV + nSV);
                                writer.print(".. ");
                                writer.print(nV);
                                writer.println("]");
                                writer.print("m = ");
                                writer.println(mV);
                                writer.print("d = ");
                                writer.println(dV);
                                break;
                            }
                            case 2: {
                                writer.print("changing m by [");
                                writer.print(mMinV);
                                writer.print(", ");
                                writer.print(mMinV + mSV);
                                writer.print(".. ");
                                writer.print(mV);
                                writer.println("]");
                                writer.print("n = ");
                                writer.println(nV);
                                writer.print("d = ");
                                writer.println(dV);
                                break;
                            }
                            case 3: {
                                writer.print("changing d by [");
                                writer.print(dMinV);
                                writer.print(", ");
                                writer.print(dMinV + dSV);
                                writer.print(".. ");
                                writer.print(dV);
                                writer.println("]");
                                writer.print("n = ");
                                writer.println(nV);
                                writer.print("m = ");
                                writer.println(mV);
                                break;
                            }
                        }
                        writer.print("x = ");
                        writer.println(x);
                    }
                    try (OutputStream os = Files.newOutputStream(
                        resDir.resolve("metric1.txt"));
                        PrintWriter writer = new PrintWriter(os))
                    {
                        for (Measurement meas : msms) {
                            writer.println(meas.meanProcessing);
                        }
                    }
                    try (OutputStream os = Files.newOutputStream(
                        resDir.resolve("metric2.txt"));
                        PrintWriter writer = new PrintWriter(os))
                    {
                        for (Measurement meas : msms) {
                            writer.println(meas.meanInteraction);
                        }
                    }
                    try (OutputStream os = Files.newOutputStream(
                        resDir.resolve("metric3.txt"));
                        PrintWriter writer = new PrintWriter(os))
                    {
                        for (Measurement meas : msms) {
                            writer.println(meas.meanClient);
                        }
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                        f,
                        "Error saving result files: " + ex.toString(),
                        "ЕГГОГ",
                        JOptionPane.ERROR_MESSAGE);
                    runButton.setEnabled(true);
                }
                runButton.setEnabled(true);
            });
        };

        runButton.addActionListener(needToRun);
        runButton.setBounds(270, 95, 80, 20);
        f.add(runButton);

        f.setSize(400, 160);
        f.setLayout(null);
        f.setVisible(true);
    }

}
