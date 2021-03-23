ALTER TABLE form ADD COLUMN legal_clause VARCHAR(3000);
ALTER TABLE process MODIFY legal_clause VARCHAR(3000);
ALTER TABLE process_aud MODIFY legal_clause VARCHAR(3000);
