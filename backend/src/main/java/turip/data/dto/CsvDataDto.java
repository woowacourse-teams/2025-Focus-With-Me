package turip.data.dto;

public record CsvDataDto(
        String country,        // 국가
        String province,       // 도
        String city,           // 시
        String channelName,    // 유튜브 채널명
        String title,          // 영상 제목
        String url,            // 영상 링크
        String uploadedDate,   // 업로드일
        String placeName,      // 장소 이름
        String mapUrl,         // 맵 링크
        String address,        // 주소
        String latitude,       // 위도
        String longitude,      // 경도
        String category,       // 카테고리
        String visitDay,       // 여행 일차
        String visitOrder,     // 방문 순서
        String timeline        // 타임라인
) {

    public static CsvDataDto of(
            String country,
            String province,
            String city,
            String channelName,
            String title,
            String url,
            String uploadedDate,
            String placeName,
            String mapUrl,
            String address,
            String latitude,
            String longitude,
            String category,
            String visitDay,
            String visitOrder,
            String timeline
    ) {
        return new CsvDataDto(
                country.trim(),
                province.trim(),
                city.trim(),
                channelName.trim(),
                title.trim(),
                url.trim(),
                uploadedDate.trim(),
                placeName.trim(),
                mapUrl.trim(),
                address.trim(),
                latitude.trim(),
                longitude.trim(),
                category.trim(),
                visitDay.trim(),
                visitOrder.trim(),
                timeline.trim()
        );
    }
} 
