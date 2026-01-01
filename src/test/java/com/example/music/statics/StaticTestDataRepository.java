package com.example.music.statics;

public class StaticTestDataRepository {
    public final static String testJson =
            """
                {
                    "Artist(s)": "!!!",
                    "song": "Even When the Waters Cold",
                    "text": "Friends told her",
                    "Length": "03:47",
                    "emotion": "sadness",
                    "Genre": "hip hop",
                    "Album": "Thr!!!er",
                    "Release Date": "2013-04-29",
                    "Key": "D min",
                    "Tempo": 0.4378698225,
                    "Loudness (db)": 0.785065407,
                    "Time signature": "4\\/4",
                    "Explicit": "No",
                    "Popularity": "40",
                    "Energy": "83",
                    "Danceability": "71",
                    "Positiveness": "87",
                    "Speechiness": "4",
                    "Liveness": "16",
                    "Acousticness": "11",
                    "Instrumentalness": "0",
                    "Good for Party": 0,
                    "Good for Work\\/Study": 0,
                    "Good for Relaxation\\/Meditation": 0,
                    "Good for Exercise": 0,
                    "Good for Running": 0,
                    "Good for Yoga\\/Stretching": 0,
                    "Good for Driving": 0,
                    "Good for Social Gatherings": 0,
                    "Good for Morning Routine": 0,
                    "Similar Songs": [
                        {
                            "Similar Artist 1": "Corey Smith",
                            "Similar Song 1": "If I Could Do It Again",
                            "Similarity Score": 0.9860607848
                        },
                        {
                            "Similar Artist 2": "Toby Keith",
                            "Similar Song 2": "Drinks After Work",
                            "Similarity Score": 0.9837194774
                        },
                        {
                            "Similar Artist 3": "Space",
                            "Similar Song 3": "Neighbourhood",
                            "Similarity Score": 0.9832363508
                        }
                    ]
                }
                """
                .replace("\n", "")
                .replace("\t", "")
                .replace("\r", "");
}
