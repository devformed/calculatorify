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
VALUES ('Kalkulator kredytowy',
        'Oblicza miesięczną ratę annuitetową oraz całkowity koszt odsetek',
        $${
          "inputs": [
            {
              "type": "SLIDER",
              "id": "loan_amount",
              "name": "Kwota kredytu (PLN)",
              "minValue": 10000,
              "maxValue": 500000,
              "step": 1000,
              "order": 1
            },
            {
              "type": "NUMBER",
              "id": "interest_rate",
              "name": "Oprocentowanie roczne (%)",
              "number": 3.5,
              "precision": 2,
              "order": 3
            },
            {
              "type": "SLIDER",
              "id": "term_years",
              "name": "Okres spłaty (lata)",
              "minValue": 1,
              "maxValue": 30,
              "step": 1,
              "order": 2
            }
          ],
          "outputs": [
            {
              "formula": "ROUND_TO_N(( ${loan_amount} * (${interest_rate} / 1200)) / (1 - (1 + (${interest_rate} / 1200)) ^ (- ${term_years} * 12)), 2)",
              "precision": 2,
              "order": 1
            },
            {
              "formula": "ROUND_TO_N(${loan_amount} * (${interest_rate} / 1200) * ${term_years} * 12 - ${loan_amount}, 2)",
              "precision": 2,
              "order": 2
            }
          ]
        }$$::jsonb,
        '00000000-0000-0000-0000-000000000000');