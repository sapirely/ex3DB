import java.io.*;
import java.util.*;

public class ExternalMemoryImpl extends IExternalMemory {

	@Override
	public void sort(String in, String out, String tmpPath) {
		// TODO: Implement
		try {
//			File file = new File(in);
//			long fileSize = file.length();
			BufferedReader buffer = new BufferedReader(new FileReader(in));
			BufferedWriter outputSort = new BufferedWriter(new FileWriter("tmpPath\\step1.txt"));
//			Map<String, String> rowsMap = new HashMap<String, String>();
			List<String[]> rows = new ArrayList<String[]>();
			String line = buffer.readLine();
			int lineSize = line.length() * 2;
			int readBytes;
			String[] splitLine;
//			rowsMap.put(splitLine[0], splitLine[1]);
//			rows.add(splitLine);
			long totalReadBytes = 0;
			boolean eof = false;
//			buffer.mark(0);
			buffer.reset();
			while (totalReadBytes < 50 * Math.pow(10, 6) && !eof) {
				readBytes = 0;
				while (readBytes < 4096) {
					line = buffer.readLine();
					if (line == null || line.length() == 0) {
						eof = true;
						break;
					}
					splitLine = line.split(" ", 2);
					rows.add(splitLine);
					readBytes += lineSize;
				}
				totalReadBytes += readBytes;
			}
			rows.sort(new Comparator<String[]>() {
				@Override
				public int compare(String[] o1, String[] o2) {
					if (o1[0].compareTo(o2[0]) < 0) {
						return -1;
					} else if (o1[0].compareTo(o2[0]) > 0) {
						return 1;
					} else {
						return o1[1].compareTo(o2[1]);
					}
				}
			});
			for (String[] row: rows) {
				outputSort.write(Arrays.toString(row));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void join(String in1, String in2, String out, String tmpPath) {
		
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void select(String in, String out, String substrSelect, String tmpPath) {
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void joinAndSelectEfficiently(String in1, String in2, String out,
			String substrSelect, String tmpPath) {
		
		// TODO Auto-generated method stub
		
	}


}