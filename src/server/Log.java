package server;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.ListIterator;

public class Log {
    private LinkedList<PrintStream> outputStreams;

    public Log() {
        outputStreams = new LinkedList<PrintStream>();
    }

    public void addPrintStream(PrintStream out) {
        outputStreams.add(out);
    }

    public void log(String msg) {
        ListIterator<PrintStream> iter = outputStreams.listIterator();
        while (iter.hasNext()) {
            PrintStream out = iter.next();
            out.println(msg);
            out.flush();
        }
    }
}
