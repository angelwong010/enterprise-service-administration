-- User administrative data (linked to Keycloak by keycloak_id)
CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_id VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255),
    company VARCHAR(255),
    birthday DATE,
    address TEXT,
    notes TEXT,
    avatar_url TEXT,
    background_url TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Permissions (application-level concept, stored in DB)
CREATE TABLE permissions (
    id VARCHAR(100) PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL
);

-- Role-Permission mapping (role_name = Keycloak role name)
CREATE TABLE role_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_name VARCHAR(255) NOT NULL,
    permission_id VARCHAR(100) NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE(role_name, permission_id)
);

-- Tags for user categorization
CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(100) NOT NULL
);

-- User-Tag mapping
CREATE TABLE user_tags (
    user_profile_id UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (user_profile_id, tag_id)
);

-- Indexes
CREATE INDEX idx_user_profiles_keycloak_id ON user_profiles(keycloak_id);
CREATE INDEX idx_role_permissions_role_name ON role_permissions(role_name);
