import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ExternalMemoryImpl extends IExternalMemory {

    @Override
    public void sort(String in, String out, String tmpPath) {
        // TODO: Implement
        try {
            File tempFile = File.createTempFile("step1_", ".txt", new File(tmpPath));
            tempFile.deleteOnExit();
            BufferedReader buffer = new BufferedReader(new FileReader(in));
            BufferedWriter outputSort = new BufferedWriter(new FileWriter(tempFile));
            BufferedWriter output = new BufferedWriter(new FileWriter(out));
            List<String> rows = new ArrayList<String>();
            String line = buffer.readLine();
            long lineSize = line.length() * 2;
            int readBytes;
            String[] splitLine;
            long totalReadBytes = 0;
            boolean eof = false;
            boolean blockRowsFlag = false;
            buffer = new BufferedReader(new FileReader(in));

            int numberOfBlockSets = 0;
            int numOfRowsInBlockSet = 0;
            int numberOfRowsInBlock = 0;

            while (!eof) {
                numberOfBlockSets++;
                while (totalReadBytes < 30 * Math.pow(10, 6) && !eof) {
                    readBytes = 0;
                    while (readBytes < 4096) {
                        line = buffer.readLine();
                        if (line == null || line.length() == 0) {
                            eof = true;
                            break;
                        }
                        rows.add(line);
                        readBytes += lineSize;
                    }
                    if (!blockRowsFlag) {
                        numberOfRowsInBlock = rows.size();
                        blockRowsFlag = true;
                    }
                    totalReadBytes += readBytes;
                }
                if (numberOfBlockSets == 1) {
                    numOfRowsInBlockSet = rows.size();
                }
                rows.sort(new RowComparator());

                for (String row : rows) {
                    outputSort.write(row + "\n");
                }
                outputSort.flush();
                rows.clear();
                totalReadBytes = 0;
            }
            long[] pointers = new long[numberOfBlockSets];
            for (int i = 0; i < numberOfBlockSets; i++) {
                pointers[i] = i * numOfRowsInBlockSet;
            }

            BufferedReader[] bufferFirstStep = new BufferedReader[numberOfBlockSets];
            int k = 0;
            long rowSizeInChars = (lineSize / 2) + 1;
            String[] pointersValues = new String[numberOfBlockSets];
            List<String> outputRows = new ArrayList<String>();
            ArrayList<String> values;

            // Init buffered readers
            for (int i = 0; i < numberOfBlockSets; i++) {
                bufferFirstStep[i] = new BufferedReader(new FileReader(tempFile));
                bufferFirstStep[i].skip(pointers[i] * rowSizeInChars);
            }

            int min_index = -1;
            long[] pointersCounter = new long[numberOfBlockSets];

            while (k < numOfRowsInBlockSet * numberOfBlockSets) {
                if (min_index == -1) {
                    for (int i = 0; i < numberOfBlockSets; i++) {
                        line = bufferFirstStep[i].readLine();
                        pointersValues[i] = line;
                        pointersCounter[i]++;
                    }

                } else {
                    if (pointersCounter[min_index] < numOfRowsInBlockSet) {
                        line = bufferFirstStep[min_index].readLine();
                        pointersValues[min_index] = line;
                        pointersCounter[min_index]++;
                    } else {
                        pointersValues[min_index] = null;
                    }
                }

                values = new ArrayList<String>(Arrays.asList(pointersValues));
                boolean allNull = true;
                for (String strArray : values) {
                    if (strArray != null) {
                        allNull = false;
                    }
                }
                if (allNull) {
                    break;
                }
                min_index = __find_min_index(values);
                outputRows.add(values.get(min_index));

                k++;
                if (k % numberOfRowsInBlock == 0) {
                    for (String row : outputRows) {
                        output.write(row + "\n");
//                        output.newLine();
//                        output.write("\n");
                    }
                    output.flush();
                    outputRows.clear();
                }
            }
            buffer.close();
            output.close();
            outputSort.close();
            // Closes buffered readers
            for (int i = 0; i < numberOfBlockSets; i++) {
                bufferFirstStep[i].close();
            }
//            System.out.println(k);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int __find_min_index(List<String> values) {
        int index = 0;
        RowComparator comparator = new RowComparator();
        for (int i = 1; i < values.size(); i++) {
            if (comparator.compare(values.get(i), values.get(index)) < 0) {
                index = i;
            }
        }
        return index;
    }

    @Override
    protected void join(String in1, String in2, String out, String tmpPath) {
//        sort(in1, "sorted_r.txt", tmpPath);
//        sort(in2, "sorted_s.txt", tmpPath);

//        BufferedReader tr_buffer = new BufferedReader[3];
        try {
            BufferedReader tr_buffer = new BufferedReader(new FileReader(in1));
            BufferedReader ts_buffer = new BufferedReader(new FileReader(in2));
//            BufferedReader gs_buffer = new BufferedReader(new FileReader(in2));
            BufferedWriter outputJoin = new BufferedWriter(new FileWriter(out));

            boolean tr_eof = false;
            boolean ts_eof = false;
//            boolean gs_eof = false;

            String tr = tr_buffer.readLine();
            String ts = ts_buffer.readLine();
//            String gs = gs_buffer.readLine();
            int numOfRowsInBlock = 4096 / (tr.length() * 2);

            ArrayList<String> output = new ArrayList<>();

            while (!tr_eof) {
                while (!tr_eof && compareByKey(tr, ts) < 0) {
                    tr = tr_buffer.readLine();
                    if (tr == null || tr.length() == 0) {
                        tr_eof = true;
                    }
                }
//                while (!ts_eof && compareByKey(tr, gs) > 0) {
//                    gs = gs_buffer.readLine();
//                    if (gs == null || gs.length() == 0) {
//                        gs_eof = true;
//                    }
//                }
//                while (!tr_eof && compareByKey(tr, gs) == 0) {
//                    ts_buffer = deepcopy(gs_buffer)
//                    ts = gs;
//                    // maybe ts = ts_buffer.readLine()
                while (!ts_eof && compareByKey(ts, tr) == 0) {
                    output.add(tr + " " + ts.split(" ", 2)[1]);
                    ts = ts_buffer.readLine();
                    if (ts == null || ts.length() == 0) {
                        ts_eof = true;
                    }
//                        String[] tr_split = tr.split(" ",2)
//                    }
                    if (output.size() == numOfRowsInBlock) {
                        for (String row : output) {
                            outputJoin.write(row + "\n");
                        }
                        output.clear();
                        outputJoin.flush();
                    }
                }
                tr = tr_buffer.readLine();
                if (tr == null || tr.length() == 0) {
                    tr_eof = true;
                }

//                }
//                gs_buffer = ts_buffer;
//                gs = ts;
            }

            if (output.size() > 0) {
                for (String row : output) {
                    outputJoin.write(row + "\n");
                }
                output.clear();
                outputJoin.flush();
            }

            tr_buffer.close();
            ts_buffer.close();
            outputJoin.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int compareByKey(String row1, String row2) {
        if (row1 == null) {
            return 1;
        } else if (row2 == null) {
            return -1;
        }
        String row1Key = row1.split(" ", 2)[0];
        String row2Key = row2.split(" ", 2)[0];
        return row1Key.compareTo(row2Key);
    }

    @Override
    protected void select(String in, String out, String substrSelect, String tmpPath) {
        // TODO Auto-generated method stub
        try {
            BufferedReader reader = new BufferedReader(new FileReader(in));
            BufferedWriter writer = new BufferedWriter(new FileWriter(out));
            boolean eof = false;

            while (!eof) {
                String line = reader.readLine();
                if (line == null || line.length() == 0) {
                    eof = true;
                    break;
                }
                String id = line.split(" ", 2)[0];
                if (id.contains(substrSelect)) {
                    writer.write(line + "\n");
                }
            }

            reader.close();
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void joinAndSelectEfficiently(String in1, String in2, String out,
                                         String substrSelect, String tmpPath) {

        // TODO Auto-generated method stub

    }


}