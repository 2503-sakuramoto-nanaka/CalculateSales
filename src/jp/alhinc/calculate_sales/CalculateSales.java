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
	private static final String FILE_NAME_NOT_SERIALNUMBER = "売上ファイル名が連番になっていません";
	private static final String SALEAMOUNT_OVER_10DIGITS = "合計⾦額が10桁を超えました";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 *void：戻り値なし
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
		//▲コマンドライン引数が1つ設定されていなかった場合は、
		//▲エラーメッセージ「予期せぬエラーが発生しました」をコンソールに表示
		if (args.length != 1) {
		   System.out.println(UNKNOWN_ERROR);
		}
		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		File[] files = new File(args[0]).listFiles();
		List<File> rcdFiles = new ArrayList<>();

		for(int i = 0; i<files.length ; i++) {
			//▲対象がファイルであり、「数字8桁.rcd」なのか判定します。
			if(files[i].isFile() && files[i].getName().matches("^[0-9]{8}[.]rcd$")) {
			}
			//●売上集計課題ファイルの中から、支店別売上ファイルのみ、rcdFilesという新しいリストに追加
			if(files[i].getName().matches("^[0-9]{8}[.]rcd$")) {
				rcdFiles.add(files[i]);
			}
		}
		//●ここから保持した売上ファイルの読み込みを行う(rcdFiles)
		BufferedReader br = null;

		//▲売上ファイルが連番になっていない場合は、
		//▲エラーメッセージ「売上ファイル名が連番になっていません」を表示し処理を終了
		//▲まず、比較する2つのファイル名の先頭から数字の8文字を切り出し、int型に変換
		for(int i = 0; i < rcdFiles.size() - 1; i++) {
			int former = Integer.parseInt(rcdFiles.get(i).getName().substring(0, 8));
			int latter = Integer.parseInt(rcdFiles.get(i+1).getName().substring(0, 8));
			//▲2つのファイル名の数字を比較して、差が1ではなかったら、エラーメッセージをコンソールに表示
			if((latter - former) != 1) {
				System.out.println(FILE_NAME_NOT_SERIALNUMBER);
				return;
			}
		}
		//売上ファイルの数だけ　処理を繰り返します
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
				//●ここから読み取った中身のチェックをしている
				//▲支店情報を保持しているMapに売上ファイルの支店コードが存在しなかった場合は
			    //▲エラーメッセージを「<該当ファイル名>の支店コードが不正です」を表示し、処理を終了
				if (!branchNames.containsKey(sales.get(0))) {
				     System.out.println(rcdFiles.get(i).getName() + "の支店コードが不正です");
				     //▲エラーのまま進めると、その先でもエラーが起きてしまうから終了させる
				     return;
				}
				//▲売上ファイルの行数が2行ではなかった場合は、
			    //▲エラーメッセージ「<該当ファイル名>のフォーマットが不正です」を表示し、終了
				if(sales.size() != 2) {
				    System.out.println(rcdFiles.get(i).getName() + "のフォーマットが不正です");
				    return;
				}
				//▲売上⾦額が数字ではなかった場合は、
			    //▲エラーメッセージ「予期せぬエラーが発生しました」をコンソールに表示
				if(!sales.get(1).matches("[0-9]")) {
					System.out.println(UNKNOWN_ERROR);
					return;
				}
				//●売上ファイルから読み込んだ売上金額をlong型へ変換を行う
				long fileSale = Long.parseLong(sales.get(1));

				//●既にMapにある売上⾦額を、売上ファイルに読み込んだ支店コードをkeyとして
				//●売上ファイルに読み込み型変換した売上金額と足す
				Long saleAmount = branchSales.get(sales.get(0)) + fileSale;

				//●加算した売上⾦額を、売上ファイルに読み込んだ支店コードをkeyとしてMapに追加
				branchSales.put(sales.get(0), saleAmount);

				//▲合計⾦額が10桁を超えた場合、エラーメッセージ「合計⾦額が10桁を超えました」を表示し、処理を終了
				if(saleAmount >= 10000000000L){
					System.out.println(SALEAMOUNT_OVER_10DIGITS);
					return;
				}
			} catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;
			} finally {
				// ファイルを開いている場合
				if(br != null) {
					try {
						// ファイルを閉じる
						br.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
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
	 * @return 読み込み可否　戻り値：boolean型で返す
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			//▲支店定義ファイルが存在しない場合は、エラーメッセージ「支店定義ファイルが存在しません」を表示し処理を終了
			if(!file.exists()) {
				System.out.println(FILE_NOT_EXIST);
				return false;
			}
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				String[] items = line.split(",");
				branchNames.put(items[0], items[1]);
				branchSales.put(items[0], 0L);

				//▲支店定義ファイルのフォーマット(支店コードが3桁で[,]区切りであること)が不正な場合は、
				//▲エラーメッセージ「支店定義ファイルのフォーマットが不正です」を表示し、処理を終了
				if((items.length != 2) || (!items[0].matches("^[0-9]{3}"))){
					System.out.println(FILE_INVALID_FORMAT);
					return false;
				}
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
