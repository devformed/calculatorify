CREATE TABLE IF NOT EXISTS calculator_
(
    id_          UUID PRIMARY KEY            NOT NULL DEFAULT gen_random_uuid(),
    title_       VARCHAR(255)                NOT NULL,
    description_ TEXT,
    config_      JSONB                       NOT NULL,
    created_at_  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at_  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    user_id_     UUID                        NOT NULL
        CONSTRAINT user__fl REFERENCES user_ (id_) ON DELETE CASCADE
);

INSERT INTO calculator_ (title_,
                         description_,
                         config_,
                         user_id_)
VALUES ('BMI Calculator',
        'Oblicza wskaźnik masy ciała (BMI)',
        $${
          "inputs": [
            {
              "type": "NUMBER",
              "id": "weight",
              "name": "Waga (kg)",
              "number": 70,
              "precision": 1,
              "order": 1
            },
            {
              "type": "NUMBER",
              "id": "height",
              "name": "Wzrost (cm)",
              "number": 175,
              "precision": 1,
              "order": 1
            }
          ],
          "outputs": [
            {
              "name": "Wskaźnik masy ciała (BMI)",
              "formula": "ROUND_TO_N(${weight} / POW(${height} / 100, 2), 1)",
              "precision": 1,
              "order": 1
            }
          ]
        }$$::jsonb,
        '00000000-0000-0000-0000-000000000000');

INSERT INTO calculator_ (title_,
                         description_,
                         config_,
                         user_id_)
VALUES ('Kalkulator Kredytowy',
        'Oblicza miesięczną ratę kredytu na podstawie kwoty, oprocentowania i okresu spłaty.',
        $${
          "inputs": [
            {
              "type": "NUMBER",
              "id": "loan_amount",
              "name": "Kwota kredytu (PLN)",
              "order": 1,
              "number": 300000,
              "precision": 2
            },
            {
              "type": "NUMBER",
              "id": "interest_rate",
              "name": "Oprocentowanie roczne (%)",
              "order": 2,
              "number": 8,
              "precision": 2
            },
            {
              "type": "NUMBER",
              "id": "years",
              "name": "Okres (lata)",
              "order": 3,
              "number": 30,
              "precision": 0
            }
          ],
          "outputs": [
            {
              "name": "Rata miesięczna",
              "formula": "ROUND_TO_N(${loan_amount} * (${interest_rate} / 1200) / (1 - POW(1 + (${interest_rate} / 1200), -${years} * 12)), 2)",
              "precision": 2,
              "order": 1
            }
          ]
        }$$::jsonb,
        '00000000-0000-0000-0000-000000000000');