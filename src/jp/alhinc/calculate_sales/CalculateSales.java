package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		File[] files = new File(args[0]).listFiles();

		List<File> rcdFiles = new ArrayList<>();

		for(int i = 0; i<files.length ; i++) {

		    if(files[i].getName().matches("^[0-9]{8}[.]rcd$")) {
		    	rcdFiles.add(files[i]);
		    }
		}
		//●ここから保持した売上ファイルの読み込みを行う(rcdFiles)
		BufferedReader br = null;
		for(int i = 0 ; i < rcdFiles.size(); i++) {

        	try {
        		File file = new File(args[0], rcdFiles.get(i).getName()) ;
        		FileReader fr = new FileReader(file);
        		br = new BufferedReader(fr);

        		String line;
        		//●salesという新しいListを作る
        		ArrayList<String> sales = new ArrayList<String>();
        		//●1行ずつ読み込みlineに代入

        		while((line = br.readLine()) != null) {
        			//●読み込んだ情報をsalesに追加
        			sales.add(line);
        		}

        		//●売上ファイルから読み込んだ売上金額をlong型へ変換を行う
        		long fileSale = Long.parseLong(sales.get(1));

        		//●既にMapにある売上⾦額を、売上ファイルに読み込んだ支店コードをkeyとして
        		//●売上ファイルに読み込み型変換した売上金額と足す
        		Long saleAmount = branchSales.get(sales.get(0)) + fileSale;
			    //●加算した売上⾦額を、売上ファイルに読み込んだ支店コードをkeyとしてMapに追加
			    branchSales.put(sales.get(0), saleAmount);

        	} catch(IOException e) {
    			System.out.println(UNKNOWN_ERROR);
    			return ;
    		} finally {
    			// ファイルを開いている場合
    			if(br != null) {
    				try {
    					// ファイルを閉じる
    					br.close();
    				} catch(IOException e) {
    					System.out.println(UNKNOWN_ERROR);
    					return ;
    				}
    			}
    		}
    	}
		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}
	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
			// ※ここの読み込み処理を変更してください。(処理内容1-2)
				String[] items = line.split(",");

				branchNames.put(items[0], items[1]);
				branchSales.put(items[0], 0L);
			}
		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		//●作成した支店別集計ファイルに書き込む
		BufferedWriter bw = null;

		try {
			File file = new File(path, fileName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			//●MapからKeyの⼀覧を取得してKeyの数だけ繰り返す
			for (String key : branchNames.keySet()) {
				//●支店コードをキーとして支店名と合計金額を書き込む
				bw.write(key + "," + branchNames.get(key) + "," + branchSales.get(key));
				//●支店ごとに改行
				bw.newLine();
			}
		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(bw != null) {
				try {
					// ファイルを閉じる
					bw.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	  }
    }
