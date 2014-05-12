----- QUESTION SET ----

drop table if exists mm_patch;
create table mm_patch(
    patch INTEGER PRIMARY KEY,
    downloaded BOOL
);

drop table if exists mm_question;
create table mm_question(
    id_question INTEGER PRIMARY KEY AUTOINCREMENT,
    txt_question TEXT,                      --Question
    txt_0 TEXT,                             --Answer A, Correct Answer
    txt_1 TEXT,                             --Answer B
    txt_2 TEXT,                             --Answer C
    txt_3 TEXT,                             --Answer D
    movie TEXT,                             --Movie text 
    tag TEXT,                           -- coma delimited
    difficulty TINYINT,
    earned BOOL,
    year INTEGER,
    fk_cat TEXT,
    fk_patch REFERENCES mm_patch(patch)
);

---- GEARS ----
drop table if exists mm_gear;
create table mm_gear(
    tag TEXT PRIMARY KEY,
    enabled BOOL,           
    bought BOOL              
);

drop table if exists mm_level;
create table mm_level(
    tag TEXT PRIMARY KEY,
    score   TINYINT,                --The rating of the level
    combo   INTEGER,                -- Max combo
    unlocked BOOL,                  --Career level unlocked or not
    bought BOOL                     --Career level bought or not
);

drop table if exists mm_pack;
create table mm_pack(
    name TEXT PRIMARY KEY,                  --Pack name, usually movie, or series
    description TEXT,
    isNew BOOL,
    deluxe BOOL,
    bought BOOL,
    price INTEGER,
    score INTEGER,
    bounty INTEGER,
    fk_patch REFERENCES mm_patch(patch),
    fk_genre TEXT,
    year INTEGER
);


drop table if exists mm_challenge;
create table mm_challenge(
    id_question INTEGER PRIMARY KEY AUTOINCREMENT,
    txt_question TEXT,                      --Question
    txt_0 TEXT,                             --Answer A, Correct Answer
    txt_1 TEXT,                             --Answer B
    txt_2 TEXT,                             --Answer C
    txt_3 TEXT,                             --Answer D
    fk_pack REFERENCES mm_pack(name),    --Quiz Pack ID 
    tag TEXT,                               -- coma delimited
    difficulty TINYINT,
    earned BOOL                             --question answered
);

drop table if exists mm_trivia;
create table mm_trivia(
    id_trivia INTEGER PRIMARY KEY AUTOINCREMENT,
    fk_pack REFERENCES mm_pack(name),       --Quiz pack ID
    trivia TEXT,                            --Trivia
    earned BOOL,                             --Answered
    serial INTEGER                          --Trivia serial number
);

drop table if exists mm_badge;
create table mm_badge(
    id_badge INTEGER PRIMARY KEY AUTOINCREMENT,
    fk_pack REFERENCES mm_pack(name),       --Quiz pack ID
    badgeName TEXT,                         --Name of the badge
    url TEXT,                               --Picture url
    description TEXT,
    earned BOOL,
    isNew BOOL
);

------ LEVEL INITIALIZATION -------
insert or ignore into mm_level values("1",0,0,0,0);
insert or ignore into mm_level values("2",0,0,0,0);
insert or ignore into mm_level values("3",0,0,0,0);
insert or ignore into mm_level values("4",0,0,0,0);
insert or ignore into mm_level values("5",0,0,0,0);
insert or ignore into mm_level values("6",0,0,0,0);

insert or ignore into mm_gear values("2",0,0);
insert or ignore into mm_gear values("3",0,0);
insert or ignore into mm_gear values("4",0,0);
insert or ignore into mm_gear values("5",0,0);

insert or ignore into mm_patch values(0,1);

