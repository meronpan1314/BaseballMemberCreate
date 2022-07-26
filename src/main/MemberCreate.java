package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import common.dto.Player;
import common.scraping.MemberCreateScraping;

public class MemberCreate {

	public static void main(String[] args) throws IOException {
		// CSVファイルの名前を設定する
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		String executionTime = format.format(LocalDateTime.now());
		String fileName = "D:\\baseball\\baseballMember_" + executionTime + ".csv";

		// チームごとのURLを設定する
		Map<String, String> teamMap = new HashMap<>();
		teamMap.put("東京ヤクルトスワローズ", "2");
		teamMap.put("阪神タイガース", "5");
		teamMap.put("読売ジャイアンツ", "1");
		teamMap.put("広島東洋カープ", "6");
		teamMap.put("中日ドラゴンズ", "4");
		teamMap.put("横浜DeNAベイスターズ", "3");
		teamMap.put("オリックス・バファローズ", "11");
		teamMap.put("千葉ロッテマリーンズ", "9");
		teamMap.put("東北楽天ゴールデンイーグルス", "376");
		teamMap.put("福岡ソフトバンクホークス", "12");
		teamMap.put("北海道日本ハムファイターズ", "8");
		teamMap.put("埼玉西武ライオンズ", "7");
		
		FileWriter fw = new FileWriter(fileName,false);
		PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		
		List<String> playerList = new ArrayList<>();
		Player player = new Player();
		
		MemberCreateScraping scraping = new MemberCreateScraping();
		
		try {
			// ヘッダーの設定を行う
			header(pw);
			int count = 0;
			
			for (Iterator<String> itr = teamMap.keySet().iterator(); itr.hasNext();) {
				String key = itr.next();
				System.out.println(key + "の投手一覧を取得します。");
				playerList = scraping.getPlayerUrlList(teamMap.get(key), "p");
				for (String url : playerList) {
					player = scraping.getPlayerInfo(url);
					print(pw, player, key);
					Thread.sleep(3000);
					count++;
				}
				System.out.println("進捗：" + count + "件取得完了");

				System.out.println(key + "の野手一覧を取得します。");
				playerList = scraping.getPlayerUrlList(teamMap.get(key), "b");
				for (String url : playerList) {
					player = scraping.getPlayerInfo(url);
					print(pw, player, key);
					Thread.sleep(3000);
					count++;
				}
				System.out.println("進捗：" + count + "件取得完了");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pw.close();
			System.out.println("処理終了しました。");
		}
	}

	/**
	 * CSVファイルのヘッダーを設定する
	 * @param pw
	 */
	private static void header(PrintWriter pw) {
		pw.print("選手名");
		pw.print(",");
		pw.print("フリガナ");
		pw.print(",");
		pw.print("所属チーム");
		pw.print(",");
		pw.print("背番号");
		pw.print(",");
		pw.print("ポジション");
		pw.print(",");
		pw.print("出身地");
		pw.print(",");
		pw.print("誕生日");
		pw.print(",");
		pw.print("身長");
		pw.print(",");
		pw.print("体重");
		pw.print(",");
		pw.print("血液型");
		pw.print(",");
		pw.print("利き手（投げ方）");
		pw.print(",");
		pw.print("利き手（打ち方）");
		pw.print(",");
		pw.print("ドラフト年");
		pw.print(",");
		pw.print("ドラフト順位");
		pw.println();
	}

	/**
	 * 選手情報をCSVファイルに入力する
	 * @param pw
	 * @param player
	 * @param teamName
	 */
	private static void print(PrintWriter pw, Player player, String teamName) {
		pw.print(player.getNameKanji());
		pw.print(",");
		pw.print(player.getNameKana());
		pw.print(",");
		pw.print(teamName);
		pw.print(",");
		pw.print(player.getUniformNumber());
		pw.print(",");
		pw.print(player.getPosition());
		pw.print(",");
		pw.print(player.getBirthplace());
		pw.print(",");
		pw.print(player.getBirthday());
		pw.print(",");
		pw.print(player.getHeight());
		pw.print(",");
		pw.print(player.getWeight());
		pw.print(",");
		pw.print(player.getBloodType());
		pw.print(",");
		pw.print(player.getDominantHand());
		pw.print(",");
		pw.print(player.getDominantdrop());
		pw.print(",");
		pw.print(player.getDraftYear());
		pw.print(",");
		pw.print(player.getDraftRank());
		pw.println();
	}
}