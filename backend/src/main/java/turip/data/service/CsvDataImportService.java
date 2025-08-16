package turip.data.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.category.domain.Category;
import turip.category.repository.CategoryRepository;
import turip.city.domain.City;
import turip.city.repository.CityRepository;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.contentplace.domain.ContentPlace;
import turip.contentplace.repository.ContentPlaceRepository;
import turip.country.domain.Country;
import turip.country.repository.CountryRepository;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
import turip.data.dto.CsvDataDto;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;
import turip.placecategory.domain.PlaceCategory;
import turip.placecategory.repository.PlaceCategoryRepository;
import turip.province.domain.Province;
import turip.province.repository.ProvinceRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CsvDataImportService {

    private static final String QUOTATION_MARKS = "\"";
    private static final String COMMA_SPLIT_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;
    private final CreatorRepository creatorRepository;
    private final ContentRepository contentRepository;
    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceCategoryRepository placeCategoryRepository;
    private final ContentPlaceRepository contentPlaceRepository;

    public void importCsvData(String csvFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            reader.readLine(); // 헤더 건너뛰기

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = parseCsvLine(line);
                if (fields != null) {
                    CsvDataDto csvData = CsvDataDto.of(
                            fields[0], fields[1], fields[2], fields[3], fields[4],
                            fields[5], fields[6], fields[7], fields[8], fields[9],
                            fields[10], fields[11], fields[12], fields[13], fields[14], fields[15]
                    );
                    processCsvData(csvData);
                }
            }

            log.info("CSV 데이터 임포트가 완료되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 읽기 중 오류 발생", e);
        } catch (Exception e) {
            throw new RuntimeException("CSV 파일 처리 중 오류 발생", e);
        }
    }

    private String[] parseCsvLine(String line) {
        String[] fields = line.split(COMMA_SPLIT_REGEX); // 쉼표로 분리

        if (fields.length < 16) {
            log.warn("CSV 라인이 예상보다 짧습니다: {}", line);
            return null;
        }

        // 따옴표 제거
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] != null && fields[i].startsWith(QUOTATION_MARKS) && fields[i].endsWith(QUOTATION_MARKS)) {
                fields[i] = fields[i].substring(1, fields[i].length() - 1);
            }
        }

        return fields;
    }

    private void processCsvData(CsvDataDto csvData) {
        log.info("CSV 데이터 처리 시작: {}", csvData.title());

        Country country = findOrCreateCountry(csvData.country());
        if (country != null) {
            log.info("Country 처리: {}", country.getName());
        }

        Province province = findOrCreateProvince(csvData.province());
        if (province != null) {
            log.info("Province 처리: {}", province.getName());
        }

        City city = findOrCreateCity(country, province, csvData.city());
        if (city != null) {
            log.info("City 처리: {}", city.getName());
        }

        Creator creator = findOrCreateCreator(csvData.channelName());
        if (creator != null) {
            log.info("Creator 처리: {}", creator.getChannelName());
        }

        Content content = findOrCreateContent(creator, city, csvData);
        if (content != null) {
            log.info("Content 처리: {}", content.getTitle());
        }

        Place place = findOrCreatePlace(csvData);
        if (place != null) {
            log.info("Place 처리: {}", place.getName());
        }

        Category category = findOrCreateCategory(csvData.category());
        if (category != null) {
            log.info("Category 처리: {}", category.getName());
        }

        PlaceCategory placeCategory = findOrCreatePlaceCategory(place, category);
        if (placeCategory != null) {
            log.info("PlaceCategory 처리 완료");
        }

        if (csvData.visitDay() != null && !csvData.visitDay().isEmpty() &&
                csvData.visitOrder() != null && !csvData.visitOrder().isEmpty()) {
            ContentPlace contentPlace = getOrCreateContentPlace(csvData, place, content);
            if (contentPlace != null) {
                log.info("ContentPlace 처리: day={}, order={}", contentPlace.getVisitDay(), contentPlace.getVisitOrder());
            }
        }

        log.info("CSV 데이터 처리 완료: {}", csvData.title());
    }

    private Country findOrCreateCountry(String countryName) {
        if (isNullOrEmpty(countryName)) {
            return null;
        }
        return countryRepository.findByName(countryName)
                .orElseGet(() -> countryRepository.save(new Country(countryName, "")));
    }

    private Province findOrCreateProvince(String provinceName) {
        if (isNullOrEmpty(provinceName)) {
            return null;
        }
        return provinceRepository.findByName(provinceName)
                .orElseGet(() -> provinceRepository.save(new Province(provinceName)));
    }

    private City findOrCreateCity(Country country, Province province, String cityName) {
        if (isNullOrEmpty(cityName)) {
            return null;
        }
        return cityRepository.findByName(cityName)
                .orElseGet(() -> cityRepository.save(new City(country, province, cityName, "")));
    }

    private Creator findOrCreateCreator(String channelName) {
        if (isNullOrEmpty(channelName)) {
            return null;
        }
        return creatorRepository.findByChannelName(channelName)
                .orElseGet(() -> creatorRepository.save(new Creator(channelName, "")));
    }

    private Content findOrCreateContent(Creator creator, City city, CsvDataDto csvData) {
        if (isNullOrEmpty(csvData.title()) || isNullOrEmpty(csvData.url())) {
            return null;
        }
        return contentRepository.findByTitleAndUrl(csvData.title(), csvData.url())
                .orElseGet(() -> createAndSaveContent(creator, city, csvData));
    }

    private Content createAndSaveContent(Creator creator, City city, CsvDataDto csvData) {
        LocalDate uploadedDate = parseUploadedDate(csvData.uploadedDate());

        Content newContent = new Content(
                creator, city, csvData.title(), csvData.url(), uploadedDate
        );

        return contentRepository.save(newContent);
    }

    private LocalDate parseUploadedDate(String uploadedDate) {
        if (isNullOrEmpty(uploadedDate)) {
            return null;
        }

        try {
            return LocalDate.parse(uploadedDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new RuntimeException("업로드 날짜 파싱 실패", e);
        }
    }

    private Place findOrCreatePlace(CsvDataDto csvData) {
        String placeUrl = csvData.mapUrl();
        if (isNullOrEmpty(placeUrl)) {
            return null;
        }
        return placeRepository.findByUrl(placeUrl)
                .orElseGet(() -> createAndSavePlace(csvData));
    }

    private Place createAndSavePlace(CsvDataDto csvData) {
        double latitude = parseDoubleOrDefault(csvData.latitude(), "위도");
        double longitude = parseDoubleOrDefault(csvData.longitude(), "경도");

        Place newPlace = new Place(
                null,
                csvData.placeName(),
                csvData.mapUrl(),
                csvData.address(),
                latitude,
                longitude
        );

        return placeRepository.save(newPlace);
    }

    private double parseDoubleOrDefault(String value, String label) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.warn("{} 파싱 실패: {}", label, value);
            return 0.0;
        }
    }

    private Category findOrCreateCategory(String categoryName) {
        if (isNullOrEmpty(categoryName)) {
            return null;
        }
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
    }

    private PlaceCategory findOrCreatePlaceCategory(Place place, Category category) {
        if (place == null || category == null) {
            return null;
        }
        return placeCategoryRepository.findByPlaceAndCategory(place, category)
                .orElseGet(() -> placeCategoryRepository.save(new PlaceCategory(place, category)));
    }

    private ContentPlace getOrCreateContentPlace(CsvDataDto csvData, Place place, Content content) {
        if (place == null || content == null) {
            return null;
        }
        int visitDay = parseIntegerOrDefault(csvData.visitDay(), "방문 일차");
        int visitOrder = parseIntegerOrDefault(csvData.visitOrder(), "방문 순서");

        LocalTime timeLine = parseTimeLine(csvData.timeline());

        return contentPlaceRepository
                .findByContentAndPlaceAndVisitDayAndVisitOrder(content, place, visitDay, visitOrder)
                .orElseGet(() -> createAndSaveContentPlace(content, place, timeLine, visitDay, visitOrder));
    }

    private LocalTime parseTimeLine(String timeLine) {
        String[] parts = timeLine.split(":");
        int minute = Integer.parseInt(parts[0]);
        int second = Integer.parseInt(parts[1]);
        return LocalTime.of(0, minute, second);
    }

    private int parseIntegerOrDefault(String value, String label) {
        if (value == null || value.isEmpty()) {
            return 1;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException(label + " 파싱 실패", e);
        }
    }

    private ContentPlace createAndSaveContentPlace(Content content, Place place, LocalTime timeLine, int visitDay,
                                                   int visitOrder) {
        ContentPlace newContentPlace = new ContentPlace(visitDay, visitOrder, timeLine, place, content);
        return contentPlaceRepository.save(newContentPlace);
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
} 
