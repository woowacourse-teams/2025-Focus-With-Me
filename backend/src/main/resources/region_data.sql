-- Countries (국가)
INSERT INTO country (name, image_url)
VALUES ('대한민국', ''),
       ('일본', 'https://drive.google.com/uc?export=view&id=1XJEi-3uY6JSCPbGAHtGPp3DMsX9Fxbre'),
       ('대만', 'https://drive.google.com/uc?export=view&id=19ab6qDf2SndV1Pwd8rCMupx8qa_EIO9V'),
       ('중국', 'https://drive.google.com/uc?export=view&id=1Wjdvnzp-UEi76VJZNX068vfMfVBopCq3'),
       ('베트남', 'https://drive.google.com/uc?export=view&id=12hJnGcO6exvbM-2lZUdSaAx8wAZu864_');

-- Provinces (도/시)
INSERT INTO province (name)
VALUES ('경기'),
       ('강원'),
       ('충청'),
       ('전라'),
       ('경상'),
       ('제주'),

       ('서울'),
       ('부산'),
       ('대구'),
       ('인천'),
       ('광주'),
       ('대전'),
       ('울산'),
       ('세종');

-- Cities (도시)
-- 대한민국 - 광역시/특별시/자치시
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('서울', 1, 7, 'https://drive.google.com/uc?export=view&id=1UGj1YCxHUVjQGsHrDmYmQlCuRFkHpn92'),
       ('부산', 1, 8, 'https://drive.google.com/uc?export=view&id=1Fbins0H99JXI5nLCRGqTyIAxqRgryliZ'),
       ('대구', 1, 9, 'https://drive.google.com/uc?export=view&id=1UL3kAWg19irtiotj-fvzRkOICTt2Fn7k'),
       ('인천', 1, 10, 'https://drive.google.com/uc?export=view&id=1hsFHqJKfhvj-GgWQzfDl15OywTgdrOJ5'),
       ('광주', 1, 11, ''),
       ('대전', 1, 12, 'https://drive.google.com/uc?export=view&id=1MKR-tYsyLcNwoD0V5AHgf-3SSHaDgq_6'),
       ('울산', 1, 13, ''),
       ('세종', 1, 14, '');

-- 대한민국 - 경기도
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('수원', 1, 1, 'https://drive.google.com/uc?export=view&id=1p4D4b09pzPZ3Xp1NE_LgBIRcbwfiNCd9'),
       ('용인', 1, 1, ''),
       ('성남', 1, 1, ''),
       ('고양', 1, 1, ''),
       ('안산', 1, 1, ''),
       ('안양', 1, 1, ''),
       ('평택', 1, 1, ''),
       ('남양주', 1, 1, ''),
       ('시흥', 1, 1, ''),
       ('의정부', 1, 1, ''),
       ('파주', 1, 1, ''),
       ('김포', 1, 1, '');

-- 대한민국 - 강원
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('춘천', 1, 2, ''),
       ('원주', 1, 2, ''),
       ('강릉', 1, 2, 'https://drive.google.com/uc?export=view&id=1MuVxiOenZS5JnpW0BlsqwOGxcXT2Ncm9'),
       ('속초', 1, 2, 'https://drive.google.com/uc?export=view&id=1WUDx88PHeEyUp-pVmsxwXXD-WY92_X2O');

-- 대한민국 - 충청
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('청주', 1, 3, ''),
       ('충주', 1, 3, ''),
       ('제천', 1, 3, ''),
       ('천안', 1, 3, ''),
       ('아산', 1, 3, ''),
       ('서산', 1, 3, '');

-- 대한민국 - 전라
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('전주', 1, 4, 'https://drive.google.com/uc?export=view&id=1B6vyjYUsH7VAZYptMLjBx2fTxa_XvCKb'),
       ('익산', 1, 4, ''),
       ('군산', 1, 4, ''),
       ('여수', 1, 4, ''),
       ('순천', 1, 4, ''),
       ('목포', 1, 4, ''),
       ('광양', 1, 4, ''),
       ('나주', 1, 4, '');

-- 대한민국 - 경상
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('포항', 1, 5, ''),
       ('경주', 1, 5, 'https://drive.google.com/uc?export=view&id=1gwJkaUj1btIzsUeMdanMClhkgN9AJ0LJ'),
       ('구미', 1, 5, ''),
       ('안동', 1, 5, ''),
       ('상주', 1, 5, ''),
       ('창원', 1, 5, ''),
       ('진주', 1, 5, ''),
       ('김해', 1, 5, ''),
       ('양산', 1, 5, '');

-- 대한민국 - 제주
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('제주', 1, 6, 'https://drive.google.com/uc?export=view&id=14Z74DP4OwiOqkCNGqehUvz_4OxbC7zVh'),
       ('서귀포', 1, 6, '');

-- 일본
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('도쿄', 2, null, ''),
       ('후쿠오카', 2, null, ''),
       ('교토', 2, null, ''),
       ('오사카', 2, null, ''),
       ('나고야', 2, null, ''),
       ('삿포로', 2, null, ''),
       ('나라', 2, null, ''),
       ('가나자와', 2, null, ''),
       ('하코네', 2, null, ''),
       ('오키나와', 2, null, '');

-- 대만
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('타이페이', 3, null, ''),
       ('신베이시', 3, null, ''),
       ('타이중', 3, null, ''),
       ('가오슝', 3, null, ''),
       ('타이난', 3, null, '');

-- 중국
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('베이징', 4, null, ''),
       ('상하이', 4, null, ''),
       ('광저우', 4, null, ''),
       ('선전', 4, null, ''),
       ('청두', 4, null, ''),
       ('시안', 4, null, ''),
       ('충칭', 4, null, ''),
       ('항저우', 4, null, ''),
       ('난징', 4, null, ''),
       ('톈진', 4, null, ''),
       ('우한', 4, null, ''),
       ('리장', 4, null, ''),
       ('장자제', 4, null, ''),
       ('하얼빈', 4, null, ''),
       ('구이린', 4, null, ''),
       ('쿤밍', 4, null, ''),
       ('다리', 4, null, '');
