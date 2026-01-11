# Music App

## Tech Stack

- Java 21
- Spring Boot 3.5.9
- Spring WebFlux 6.2.15
- R2DBC 3.5.9
- MySQL 8.0.28

## Database Scheme

| Table | Description | Column |
| --- | --- | --- |
| songs | 곡 정보 | 곡명, 곡 장르, 길이, 가사 등 |
| artists | 아티스트 정보 | 아티스트 명 |
| albums | 앨범 정보 | 앨범명, 발매일, 발매 연도 |
| song_features | 곡 특징 | 템포, 박자 등 |
| song_moods | 곡 분위기 | 에너지틱, 긍정적인 등 |
| listen_context | 추천 상황 | 파티, 드라이빙 등 |
| similar_songs | 유사 곡 | 유사곡 명, 아티스트, 점수 |
| song_likes | 곡 좋아요 정보 | 곡 ID, 좋아요 시간 |
| artists_albums | 아티스트 - 앨범 매핑 정보 | 앨범 ID, 아티스트 ID |

## API Specification

| Method | Endpoint | Description | Query Parameters                                                                       | Body |
| --- | --- | --- |----------------------------------------------------------------------------------------| --- |
| GET | /music/list | 곡 목록 조회(Pagenation) | page(default 1), size(default 5), sortBy(default song_id), direction(default asc)      |  |
| GET | /music/{songId} | 곡 세부 정보 조회 |                                                                                        |  |
| GET | /music/sync | DB에 JSON 데이터 저장 |                                                                                        |  |
| GET | /musci/statistics/album/yearly/counts | 연도별 앨범 수 조회(Pagenation) | page(default 1), size(default 5), sortBy(default releas_year), direction(default desc) |  |
| GET | /music/statistics/album/artist/counts | 아티스트 별 앨범 수 조회(Pagenation) | page(default 1), size(default 5), sortBy(defautl artist_name), direction(defautl desc) |  |
| POST | /music/like | 해당 곡 좋아요 |                                                                                        | {”songId”: 1} |
| GET | /music/like/top | 1시간 이내 좋아요 TOP 조회 | top(default 10)                                                                        |  |

## Configuration
- 실행 전, 명령행 인자를 통해 3가지 값 작성
    1. --spring.r2dbc.url=r2dbc:mysql:{database url}
    2. --spring.r2dbc.username={username}
    3. --spring.r2dbc.password={password}
    4. --resource.json.path={"json file path"}