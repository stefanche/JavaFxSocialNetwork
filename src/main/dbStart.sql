CREATE TABLE user_table (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(50),
                            password VARCHAR(50)
);
--friendship table
CREATE TABLE friendship_table (
    from_user INT,
    to_user INT,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (from_user,to_user),
    accepted BOOLEAN,
    FOREIGN KEY (to_user) REFERENCES user_table(id) on delete CASCADE,
    FOREIGN KEY (from_user) REFERENCES user_table(id) on delete CASCADE);
--message
CREATE TABLE message_table (
    id SERIAL PRIMARY KEY,
    message TEXT NOT NULL,
    from_user_id INT NOT NULL,
    reply_to_message_id INT,
    CONSTRAINT fk_from_user FOREIGN KEY (from_user_id) REFERENCES user_table (id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_message FOREIGN KEY (reply_to_message_id) REFERENCES message_table (id)
);
--recipients
CREATE TABLE MessageRecipients (
    message_id INT,                    -- References Messages table
    recipient_user_id INT,             -- References Users table
    PRIMARY KEY (message_id, recipient_user_id),
    CONSTRAINT fk_message FOREIGN KEY (message_id) REFERENCES message_table (id) ON DELETE CASCADE,
    CONSTRAINT fk_recipient FOREIGN KEY (recipient_user_id) REFERENCES user_table (id) ON DELETE CASCADE
);
-- add users
INSERT INTO user_table (name, password)
VALUES
    ('Eve', 'hunter2'),
    ('Bob', '123'),
    ('Frank', 'letmein123'),
    ('Grace', 'passw0rd'),
    ('Henry', 'iloveyou456'),
    ('Ivy', 'trustno1'),
    ('Jack', '12345678'),
    ('Karen', 'football2024'),
    ('Leo', 'sunshine7'),
    ('Mia', 'princess99'),
    ('Noah', 'dragonfire'),
    ('Olivia', 'starlight8'),
    ('Paul', 'monkey123'),
    ('Quincy', 'purplehaze'),
    ('Rita', 'happyday1'),
    ('Steve', 'blueberry'),
    ('Tina', 'strongpass'),
    ('Uma', 'goldeneye'),
    ('Victor', 'password0'),
    ('Wendy', 'blackcat'),
    ('Xander', 'knight99'),
    ('Yara', 'mango789'),
    ('Zoe', 'butterfly'),
    ('Alex', 'rockstar1'),
    ('Brian', 'superman'),
    ('Clara', 'cheesecake'),
    ('David', 'redhot77'),
    ('Elena', 'dolphin88'),
    ('Fred', 'wildwest'),
    ('Gina', 'thunderbird'),
    ('Hank', 'forest12');

-- add messages
INSERT INTO message_table (message, from_user_id, reply_to_message_id) VALUES
                                                                           ('te salut', 2, null),
                                                                           ('receptionat', 3, null),
                                                                           ('hai ca merge', 2, null),
                                                                           ('ba nu', 3, null),
                                                                           ('ba da', 2, null);

-- add convos
INSERT INTO messagerecipients (message_id, recipient_user_id)
VALUES
    (7,3),
    (8,2),
    (9,3),
    (10,2);

--get convos
WITH AggregatedParticipants AS (
    SELECT
        m.id AS message_id,
        ARRAY_AGG(DISTINCT u.id ORDER BY u.id) AS participants
    FROM
        message_table m
            JOIN
        user_table u
        ON
            u.id = m.from_user_id OR u.id IN (
                SELECT recipient_user_id
                FROM MessageRecipients
                WHERE message_id = m.id
            )
    GROUP BY
        m.id
)
SELECT DISTINCT
    participants
FROM
    AggregatedParticipants
WHERE 2 = ANY (participants);


-- get message for a given convo
WITH AggregatedParticipants AS (
    SELECT
        m.id AS message_id,
        ARRAY_AGG(DISTINCT u.id ORDER BY u.id) AS participants
    FROM
        message_table m
            JOIN user_table u
                 ON u.id = m.from_user_id OR u.id IN (
                     SELECT recipient_user_id
                     FROM MessageRecipients
                     WHERE message_id = m.id
                 )
    GROUP BY
        m.id
)
SELECT
    m.id AS message_id,
    m.message AS message_body,
    m.from_user_id AS sender,
    ARRAY_AGG(DISTINCT r.recipient_user_id) AS recipients,
    m.reply_to_message_id AS reply_id,
    m.data AS data
FROM
    message_table m
        LEFT JOIN MessageRecipients r ON m.id = r.message_id
        JOIN AggregatedParticipants ap ON ap.message_id = m.id
WHERE
    ap.participants @> ARRAY[1, 2]
  AND ARRAY_LENGTH(ap.participants, 1) = 2
GROUP BY
    m.id, m.message, m.from_user_id, m.reply_to_message_id, m.data;
