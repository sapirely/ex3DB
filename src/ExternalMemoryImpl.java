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
			BufferedWriter outputSort = new BufferedWriter(new FileWriter(tmpPath + "\\step1.txt"));
			BufferedWriter output = new BufferedWriter(new FileWriter(out));
//			List<String[]> rows = new ArrayList<String[]>();
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
			int numOfRowsInBlockSet =0;
			int numberOfRowsInBlock =0;

			while (!eof) {
				numberOfBlockSets++;
//				System.out.println("Entered first loop");
				while (totalReadBytes < 48.25 * Math.pow(10, 6) && !eof) {
//					System.out.println("Entered 2nd loop");
					readBytes = 0;
					while (readBytes < 4096) {
//						System.out.println("Entered 3rd loop");
						line = buffer.readLine();
						if (line == null || line.length() == 0) {
							eof = true;
							break;
						}
//						splitLine = line.split(" ", 2);
						rows.add(line);
						readBytes += lineSize;
					}
					if (!blockRowsFlag){
						numberOfRowsInBlock = rows.size();
						blockRowsFlag = true;
					}
					totalReadBytes += readBytes;
				}
				if (numberOfBlockSets == 1){
					numOfRowsInBlockSet = rows.size();
				}
				rows.sort(new RowComparator());

				for (String row : rows) {
					outputSort.write(row);
//					outputSort.write(Arrays.toString(row)
//							.replace("[", "")
//							.replace("]","")
//							.replace(",", ""));
					outputSort.newLine();
				}
				outputSort.flush();
				rows.clear();
				totalReadBytes = 0;
			}
			long[] pointers = new long[numberOfBlockSets];
			for (int i = 0; i < numberOfBlockSets; i++) {
				pointers[i] = i * numOfRowsInBlockSet;
				System.out.println(Long.toString(pointers[i]));
			}

//			BufferedReader bufferFirstStep = new BufferedReader(new FileReader(tmpPath + "\\step1.txt"));
			BufferedReader[] bufferFirstStep = new BufferedReader[numberOfBlockSets];
			int k = 0;
			long rowSizeInChars = (lineSize / 2) + 2;
			String[] pointersValues = new String[numberOfBlockSets];
			List<String> outputRows = new ArrayList<String>();
			ArrayList<String> values;

			// Init buffered readers
			for (int i = 0; i < numberOfBlockSets; i++) {
				bufferFirstStep[i] = new BufferedReader(new FileReader(tmpPath + "\\step1.txt"));
				bufferFirstStep[i].skip(pointers[i] * rowSizeInChars);
			}

			int min_index = -1;

			while (k < numOfRowsInBlockSet * numberOfBlockSets) {
//				if (k % 1000 == 0){
//					System.out.println("k: "+k);
//				}
				if (min_index == -1) {
					for (int i = 0; i < numberOfBlockSets; i++) {
						line = bufferFirstStep[i].readLine();
						pointersValues[i] = line;
					}
//					bufferFirstStep.mark(0);
//					if ((pointers[i] / numOfRowsInBlockSet) % numberOfBlockSets == i) {
//						bufferFirstStep.skip(pointers[i] * rowSizeInChars);
//						line = bufferFirstStep[i].readLine();
//					} else {
//						line = null;
//					}
//					pointersValues[i] = line;
//					bufferFirstStep.reset();
//					bufferFirstStep = new BufferedReader(new FileReader(tmpPath + "\\step1.txt"));
//					System.out.println(Arrays.toString(pointersValues[i]));
				} else {
					line = bufferFirstStep[min_index].readLine();
					pointersValues[min_index] = line;
				}
//				ArrayList<String[]> values = new ArrayList<String[]>(Arrays.asList(pointersValues));
				values = new ArrayList<String>(Arrays.asList(pointersValues));
				boolean allNull = true;
				for (String strArray: values) {
					if (strArray != null)
					{
						allNull = false;
					}
				}
				if (allNull) {
					break;
				}
				min_index = __find_min_index(values);
				outputRows.add(values.get(min_index));
//						.replace("[", "")
//						.replace("]","")
//						.replace(",", ""));

//				pointers[min_index]++;
				k++;
				if (k % numberOfRowsInBlock == 0) {
					for (String row: outputRows) {
						output.write(row);
						output.newLine();
					}
					output.flush();
					outputRows.clear();
				}
			}
			System.out.println(k);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private int __find_min_index(List<String> values){
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