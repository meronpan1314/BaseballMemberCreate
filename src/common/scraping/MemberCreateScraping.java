package common.scraping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.dto.Player;

public class MemberCreateScraping {

	/**
	 * 各球団の選手詳細ベージのURLを取得する
	 * @param teamCode：球団ごとのNo category：p(投手) or b(野手)
	 * @return urlList
	 * @throws IOException
	 */
	public List<String> getPlayerUrlList(String teamCode, String category) throws IOException {
		String url;
		List<String> urlList = new ArrayList<>();

		// 引数をもとに、検索するURLを作成する
		String searchTarget = "https://baseball.yahoo.co.jp/npb/teams/" + teamCode + "/memberlist?kind=" + category;
		Document doc = Jsoup.connect(searchTarget).get();

		// 取得したxmlの中から「"td.bb-playerTable__data--player"」の物を取得する
		Elements elements = doc.select("td.bb-playerTable__data--player");

		for (Element element : elements) {
			url = element.parent().toString();
			// 選手のURLをもつ項目の場合はリストに追加する
			if (url.contains("a href")) {
				// /npb/player/数字/という文字列に整形する
				String finalUrl = url.substring(url.indexOf("a href") + 8, url.indexOf("top\">"));
				// 選手コードに修正する
				String code = finalUrl.substring(12, finalUrl.length() - 1);
				urlList.add(code);
			}
		}
		return urlList;
	}

	/**
	 * 選手詳細ページから選手情報を取得する
	 * @param url；選手詳細ページのURL
	 * @return 選手情報
	 * @throws IOException
	 * @throws Exception
	 */
	public Player getPlayerInfo(String url) throws IOException, Exception {
		Player player = new Player();
		
		// 引数をもとに検索するURLを作成する
		String searchTarget = "https://baseball.yahoo.co.jp/npb/player/" + url + "/top";
		Document doc = Jsoup.connect(searchTarget).get();
		
		// 取得したxmlの中から、背番号などに関する情報を取得する
		Elements position = doc.select("div.bb-profile__info");
		
		// 取得したxmlの中から、選手名に関する情報を取得する
		Elements name = doc.select("ruby.bb-profile__name");
		
		// 取得したxmlの中から、選手の詳細情報を取得する
		Elements profile = doc.select("dl.bb-profile__list");
		
		for (Element element : position) {
			// 背番号を格納する
			player.setUniformNumber(element.text().substring(0, element.text().indexOf(" ")));
			// ポジションを格納する
			player.setPosition(element.text().substring(element.text().indexOf(" ") + 1)); 
		}

		for (Element element : name) {
			if (element.text().contains(" （")) {
				// 選手名（漢字）を格納する
				player.setNameKanji(element.text().substring(0, element.text().indexOf(" （")));
				// 選手名（カタカナ）を格納する
				player.setNameKana(element.text().substring(element.text().indexOf("（") + 1, element.text().indexOf("）")));
			} else {
				player.setNameKanji(element.text());
				player.setNameKana(null);
			}
			
		}


		for (Element element : profile) {
			String judgementString = element.text();
			// 取得した情報の内容ごとに処理を行う
			if (judgementString.contains("出身地")) {
				// 出身地を格納する
				player.setBirthplace(judgementString.substring(4));
				
			} else if (judgementString.contains("生年月日")) {
				// 誕生日を格納する
				player.setBirthday(judgementString.substring(10, judgementString.indexOf("歳）") - 3));
				
			} else if (judgementString.contains("身長")) {
				// 身長を格納する
				player.setHeight(judgementString.substring(3, 6));
				
			} else if (judgementString.contains("体重")) {
				// 体重を格納する
				player.setWeight(judgementString.substring(3, judgementString.indexOf("kg")));
				
			} else if (judgementString.contains("血液型")) {
				// 血液型を格納する
				player.setBloodType(judgementString.substring(4));
				
			} else if (judgementString.contains("投打")) {
				// 利き手を格納する
				player.setDominantHand(judgementString.substring(3, judgementString.indexOf("投げ")));
				// 打ち方を格納する
				player.setDominantdrop(judgementString.substring(judgementString.indexOf("投げ") + 2, judgementString.indexOf("打ち")));
				
			} else if (judgementString.contains("ドラフト")) {
				// ドラフト年を格納する
				player.setDraftYear(judgementString.substring(10, 14));
				// ドラフト順位を格納する
				player.setDraftRank(judgementString.substring(16, judgementString.length() - 2));
				
			}
		}
		return player;
	}
}
