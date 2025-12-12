CREATE TABLE IF NOT EXISTS entities (
                                        id TEXT PRIMARY KEY,
                                        name TEXT NOT NULL,
                                        description TEXT,
                                        createdAt TEXT NOT NULL,
                                        updatedAt TEXT NOT NULL
);