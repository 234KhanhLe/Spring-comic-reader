package backend.init;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.model.Tag;
import backend.repository.TagRepository;

@Service
public class initTag {

	@Autowired
	TagRepository tagRepo;

	public void initializeTag() {

		Tag Action = new Tag("Action", "");
		Tag Adult = new Tag("Adult", "");
		Tag Adventure = new Tag("Adventure", "");
		Tag Anime = new Tag("Anime", "");
		Tag Isekai = new Tag("Isekai", "");
		Tag Comedy = new Tag("Comedy", "");
		Tag Comic = new Tag("Comic", "");
		Tag Cooking = new Tag("Cooking", "");
		Tag Acient = new Tag("Acient", "");
		Tag Doujinshi = new Tag("Doujinshi", "");
		Tag Drama = new Tag("Drama", "");
		Tag danmei = new Tag("danmei", "");
		Tag Ecchi = new Tag("Ecchi", "");
		Tag Fantasy = new Tag("Fantasy", "");
		Tag Gender_Bender = new Tag("Gender Bender", "");
		Tag Harem = new Tag("Harem", "");
		Tag Historical = new Tag("Historical", "");
		Tag Horror = new Tag("Horror", "");
		Tag Josei = new Tag("Josei", "");
		Tag Live_action = new Tag("Live action", "");
		Tag Manga = new Tag("Manga", "");
		Tag Manhua = new Tag("Manhua", "");
		Tag Manhwa = new Tag("Manhwa", "");
		Tag Martial_Arts = new Tag("Martial Arts", "");
		Tag Mature = new Tag("Mature", "");
		Tag Mecha = new Tag("Mecha", "");
		Tag Mystery = new Tag("Mystery", "");
		Tag romance = new Tag("romance", "");
		Tag One_shot = new Tag("One shot", "");
		Tag Psychological = new Tag("Psychological", "");
		Tag Romance = new Tag("Romance", "");
		Tag School_Life = new Tag("School Life", "");
		Tag Sci_fi = new Tag("Sci-fi", "");
		Tag Seinen = new Tag("Seinen", "");
		Tag Shoujo = new Tag("Shoujo", "");
		Tag Shoujo_Ai = new Tag("Shoujo Ai", "");
		Tag Shounen = new Tag("Shounen", "");
		Tag Shounen_Ai = new Tag("Shounen Ai", "");
		Tag Slice = new Tag("Slice", "");
		Tag Smut = new Tag("Smut", "");
		Tag Soft_Yaoi = new Tag("Soft Yaoi", "");
		Tag Soft_Yuri = new Tag("Soft Yuri", "");
		Tag Sports = new Tag("Sports", "");
		Tag Supernatural = new Tag("Supernatural", "");
		Tag Children = new Tag("Children", "");
		Tag Tragedy = new Tag("Tragedy", "");
		Tag Detective = new Tag("Detective", "");
		Tag Scan = new Tag("Scan", "");
		Tag Coloured = new Tag("Coloured", "");
		Tag VietNam = new Tag("Viet Nam", "");
		Tag Webtoon = new Tag("Webtoon", "");
		Tag Time_Travel = new Tag("Time Travel", "");
		Tag Sixteen = new Tag("16+", "");

		List<Tag> tagList = Arrays.asList(Action, Adult, Adventure, Anime, Isekai, Comedy, Comic, Cooking, Acient,
				Doujinshi, Drama, danmei, Ecchi, Fantasy, Gender_Bender, Harem, Historical, Horror, Josei, Live_action,
				Manga, Manhua, Manhwa, Martial_Arts, Mature, Mecha, Mystery, romance, One_shot, Psychological, Romance,
				School_Life, Sci_fi, Seinen, Shoujo, Shoujo_Ai, Shounen, Shounen_Ai, Slice, Smut, Soft_Yaoi, Soft_Yuri,
				Sports, Supernatural, Children, Tragedy, Detective, Scan, Coloured, VietNam, Webtoon, Time_Travel,
				Sixteen);
		tagRepo.saveAll(tagList);

	}
}
