package utility;
//pahul
public class arrayOperation {

	public static Number[][] divide2Arrays(Number[][] array1, Number[][] array2) {
		int size = array1.length;
		Number[][] resultArray = new Number[size][2];

		for (int i = 0; i < size; i++) {
			resultArray[i][0] = array1[i][0];
			resultArray[i][1] = array1[i][1].floatValue() / (array2[i][1]).floatValue();
		}
		return resultArray;
	}

	public static Number[][] multiplyBy1000(Number[][] array1) {
		int size = array1.length;
		Number[][] resultArray = new Number[size][2];

		for (int i = 0; i < size; i++) {
			resultArray[i][1] = array1[i][1].floatValue() * 1000;
			resultArray[i][0] = array1[i][0];
		}

		return resultArray;
	}

	public static Number[][] divideBy1000(Number[][] array1) {
		int size = array1.length;
		Number[][] resultArray = new Number[size][2];

		for (int i = 0; i < size; i++) {
			resultArray[i][1] = array1[i][1].floatValue() / 1000;
			resultArray[i][0] = array1[i][0];
		}

		return resultArray;
	}
	
	public static Number[] generateNumbersInRange(int start, int end, int step) {
		int size = (end-start)/step + 1;
		Number[] numbersArray = new Number[size];
		numbersArray[0] = start;
		for (int i = 1; i < size; i++) {
			numbersArray[i] = numbersArray[i-1].intValue() + step;
		}
		return numbersArray;
	}
	
	public static Number[] generateNumbersInDecRange(int start, int end, int step) {
		int size = (end-start)/step + 1;
		Number[] numbersArray = new Number[size];
		numbersArray[0] = end;
		for (int i = 1; i < size ; i++) {
			numbersArray[i] = numbersArray[i-1].intValue() - step;
		}
		return numbersArray;
	}
}
