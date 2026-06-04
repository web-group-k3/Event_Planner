-- ============================================================
--  EventPlanner — Seed Data
--  Database : eventplanner (PostgreSQL)
--
--  Schéma réel (vérifié) :
--    admin           : id(serial), username, password_hash
--    room            : id(serial), name, event_id(int), [pas de adress ni capacity]
--    event           : id(serial), title, description, start_date, end_date, location
--    speaker         : id(serial), full_name, photo_url, bio, links(jsonb), event_id(int)
--    session         : id(serial), title, description, start_time, end_time,
--                      capacity(int), event_id(int), room_id(int)
--    session_speaker : session_id(int), speaker_id(int)
--    question        : id(serial), content, author_name, upvotes, created_at, session_id(int)
--
--  Admin : username=admin  password=admin123
-- ============================================================

\connect "eventplanner"

-- ── 0. Nettoyage (ordre inverse des FK) ─────────────────────
TRUNCATE session_speaker, question, session, speaker, event, room, admin
  RESTART IDENTITY CASCADE;

-- ── 1. Admin ─────────────────────────────────────────────────
-- password en clair : admin123
-- Hash BCrypt généré avec strength=10
INSERT INTO admin (username, password_hash) VALUES
  ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');
-- id généré : 1

-- ── 2. Events (avant room car room référence event) ──────────
INSERT INTO event (title, description, start_date, end_date, location) VALUES
  (
    'Tech Summit 2026',
    'Le plus grand rassemblement tech de annee. Deux jours de conferences, workshops et networking autour de IA, du cloud et du web.',
    NOW() - INTERVAL '1 day',
    NOW() + INTERVAL '1 day',
    'Paris Expo, Porte de Versailles'
  ),
  -- id=2
  (
    'Design and UX Conference',
    'Une journee dediee au design centre utilisateur, aux systemes de design et accessibilite.',
    NOW() - INTERVAL '2 hours',
    NOW() + INTERVAL '6 hours',
    'Centre Pompidou, Paris'
  ),
  -- id=3
  (
    'Open Source Days',
    'Festival de open source : contributions, mainteneurs et gouvernance de projets communautaires.',
    NOW() + INTERVAL '7 days',
    NOW() + INTERVAL '9 days',
    'Cite des Sciences, Paris'
  );
-- ids générés : 1, 2, 3

-- ── 3. Rooms (référence event_id) ────────────────────────────
-- Chaque salle est associée à un event
-- event 1 = Tech Summit, event 2 = Design Conf, event 3 = Open Source
INSERT INTO room (name, event_id) VALUES
  ('Grande Salle',       1),  -- id=1
  ('Salle Innovation',   1),  -- id=2
  ('Auditorium Lumiere', 2),  -- id=3
  ('Atelier Creatif',    1),  -- id=4
  ('Salle Horizon',      2),  -- id=5
  ('Salle Open Source',  3);  -- id=6
-- ids générés : 1..6

-- ── 4. Speakers (référence event_id) ─────────────────────────
INSERT INTO speaker (full_name, photo_url, bio, links, event_id) VALUES
  (
    'Alice Moreau',
    'https://i.pravatar.cc/300?img=47',
    'Ingenieure IA chez DeepMind. Specialiste des LLMs et ethique algorithmique. Conferencieure internationale depuis 2019.',
    '{"linkedin": "https://linkedin.com/in/alice-moreau"}',
    1
  ),
  -- id=2
  (
    'Thomas Leroy',
    'https://i.pravatar.cc/300?img=12',
    'Architecte cloud chez AWS. 15 ans experience en systemes distribues et Kubernetes. Auteur de Cloud Native Patterns.',
    '{"twitter": "https://twitter.com/thomasleroy_dev"}',
    1
  ),
  -- id=3
  (
    'Sara El Mansouri',
    'https://i.pravatar.cc/300?img=23',
    'Lead Designer chez Figma. Experte en design systems et accessibilite WCAG. Contributrice principale a plusieurs projets open source.',
    '{"dribbble": "https://dribbble.com/sara_elm"}',
    2
  ),
  -- id=4
  (
    'Lucas Petit',
    'https://i.pravatar.cc/300?img=60',
    'Full Stack Developer et mainteneur de plusieurs packages npm populaires. Passionne de performance web et WebAssembly.',
    '{"github": "https://github.com/lucaspetit"}',
    1
  ),
  -- id=5
  (
    'Camille Dubois',
    'https://i.pravatar.cc/300?img=32',
    'Product Manager chez Stripe. Ancienne developpeuse reconvertie PM. Specialiste de la monetisation et des API financieres.',
    '{"linkedin": "https://linkedin.com/in/camille-dubois"}',
    1
  ),
  -- id=6
  (
    'Remy Fontaine',
    'https://i.pravatar.cc/300?img=15',
    'Security Engineer chez Cloudflare. Expert en cryptographie appliquee, Zero Trust et securite des APIs REST.',
    '{"twitter": "https://twitter.com/remyfontaine_sec"}',
    1
  ),
  -- id=7
  (
    'Yasmine Hamdi',
    'https://i.pravatar.cc/300?img=38',
    'Chercheuse en NLP a INRIA. Ses travaux portent sur les biais dans les modeles de langage et la generation de texte multilingue.',
    '{"scholar": "https://scholar.google.com/yasmine-hamdi"}',
    1
  ),
  -- id=8
  (
    'Baptiste Girard',
    'https://i.pravatar.cc/300?img=7',
    'DevRel Engineer chez Vercel. Createur de contenu technique, auteur de nombreux tutoriels Next.js et React Server Components.',
    '{"youtube": "https://youtube.com/@baptistegirard"}',
    3
  );
-- ids générés : 1..8

-- ── 5. Sessions ──────────────────────────────────────────────
-- Colonnes : title, description, start_time, end_time, capacity, event_id, room_id
-- LIVE    : NOW()-30min → NOW()+30min
-- PASSÉ   : fin < NOW()
-- FUTUR   : début > NOW()
INSERT INTO session (title, description, start_time, end_time, capacity, event_id, room_id) VALUES

  -- Tech Summit 2026 (event_id=1)
  (
    'Keynote : IA Generative en 2026',
    'Etat de art des LLMs, multimodalite et impacts sur industrie. Presentation des tendances cles pour les 3 prochaines annees.',
    NOW() - INTERVAL '30 minutes',
    NOW() + INTERVAL '30 minutes',   -- [LIVE]
    480, 1, 1
  ),
  -- id=2
  (
    'Kubernetes au-dela du hype',
    'Retour experience sur une migration a grande echelle vers K8s en production : les pieges, les outils et les bonnes pratiques.',
    NOW() - INTERVAL '3 hours',
    NOW() - INTERVAL '2 hours',      -- [PASSÉ]
    140, 1, 2
  ),
  -- id=3
  (
    'WebAssembly : le futur du web ?',
    'WASM pour les developpeurs web : cas usage reels, performance et interoperabilite avec JavaScript et Rust.',
    NOW() + INTERVAL '1 hour',
    NOW() + INTERVAL '2 hours',      -- [FUTUR]
    250, 1, 1
  ),
  -- id=4
  (
    'Workshop : Securiser une API REST',
    'Atelier pratique : authentification JWT, rate limiting, OWASP Top 10 et audit de securite automatise.',
    NOW() + INTERVAL '2 hours 30 minutes',
    NOW() + INTERVAL '4 hours',      -- [FUTUR]
    55, 1, 4
  ),
  -- id=5
  (
    'De Developpeuse a PM : Retour experience',
    'Comment mon background technique a transforme ma facon de faire du product management. Les competences transferables et les angles morts.',
    NOW() - INTERVAL '1 hour',
    NOW() - INTERVAL '10 minutes',   -- [PASSÉ]
    180, 1, 2
  ),

  -- Design and UX Conference (event_id=2)
  -- id=6
  (
    'Design Systems a grande echelle',
    'Comment Figma gere son propre design system : tokens, composants, gouvernance et contribution interne.',
    NOW() - INTERVAL '20 minutes',
    NOW() + INTERVAL '40 minutes',   -- [LIVE]
    320, 2, 3
  ),
  -- id=7
  (
    'Accessibilite : au-dela des checkboxes',
    'Construire des interfaces vraiment accessibles : tests avec lecteurs ecran, daltonisme, navigation clavier et ARIA.',
    NOW() + INTERVAL '1 hour 30 minutes',
    NOW() + INTERVAL '2 hours 30 minutes', -- [FUTUR]
    130, 2, 5
  ),
  -- id=8
  (
    'Motion Design pour les developpeurs',
    'Animer ses interfaces avec CSS, Framer Motion et les principes animation issus du cinema appliques au web.',
    NOW() - INTERVAL '4 hours',
    NOW() - INTERVAL '3 hours',      -- [PASSÉ]
    50, 2, 3
  ),

  -- Open Source Days (event_id=3)
  -- id=9
  (
    'Gouvernance open source : modeles et enjeux',
    'Apache, CNCF, Linux Foundation... Comment choisir son modele de gouvernance et eviter la bus factor trap.',
    NOW() + INTERVAL '7 days',
    NOW() + INTERVAL '7 days 1 hour 30 minutes', -- [FUTUR]
    290, 3, 6
  ),
  -- id=10
  (
    'Contribuer efficacement a open source',
    'De la premiere issue au maintainership : strategies, outils et communication pour devenir un contributeur de reference.',
    NOW() + INTERVAL '7 days 2 hours',
    NOW() + INTERVAL '7 days 3 hours', -- [FUTUR]
    120, 3, 6
  );
-- ids générés : 1..10

-- ── 6. Session ↔ Speakers ────────────────────────────────────
-- session 1=Keynote IA, 2=K8s, 3=WASM, 4=Workshop Sécu, 5=Dev→PM
-- session 6=Design Sys, 7=Accessibilité, 8=Motion Design
-- session 9=Gouvernance OS, 10=Contribuer OS
-- speaker 1=Alice, 2=Thomas, 3=Sara, 4=Lucas, 5=Camille, 6=Remy, 7=Yasmine, 8=Baptiste
INSERT INTO session_speaker (session_id, speaker_id) VALUES
  (1, 1),   -- Keynote IA      ← Alice Moreau
  (1, 7),   -- Keynote IA      ← Yasmine Hamdi
  (2, 2),   -- Kubernetes      ← Thomas Leroy
  (3, 4),   -- WebAssembly     ← Lucas Petit
  (4, 6),   -- Workshop Sécu   ← Remy Fontaine
  (5, 5),   -- Dev → PM        ← Camille Dubois
  (6, 3),   -- Design Systems  ← Sara El Mansouri
  (7, 3),   -- Accessibilité   ← Sara El Mansouri
  (8, 8),   -- Motion Design   ← Baptiste Girard
  (9, 4),   -- Gouvernance OS  ← Lucas Petit
  (9, 2),   -- Gouvernance OS  ← Thomas Leroy
  (10, 8);  -- Contribuer OS   ← Baptiste Girard

-- ── 7. Questions ─────────────────────────────────────────────
-- Colonne : author_name  (pas author)
-- session_id est integer
INSERT INTO question (content, author_name, upvotes, created_at, session_id) VALUES
  -- Session 1 — Keynote IA (LIVE)
  ('Quels sont les risques ethiques les plus sous-estimes des LLMs actuels ?',  'Marie T.',  12, NOW() - INTERVAL '15 minutes', 1),
  ('Est-ce que GPT-5 change vraiment la donne pour les developpeurs ?',          'Kevin L.',   8, NOW() - INTERVAL '10 minutes', 1),
  ('Comment evaluer la qualite un modele fine-tune en production ?',             'Anonyme',    5, NOW() - INTERVAL '5 minutes',  1),

  -- Session 6 — Design Systems (LIVE)
  ('Comment gerez-vous les breaking changes dans un design system partage ?',    'Julien M.', 15, NOW() - INTERVAL '12 minutes', 6),
  ('Quelle est la difference entre design tokens et variables CSS ?',            'Sophie R.',  6, NOW() - INTERVAL '8 minutes',  6),

  -- Session 2 — Kubernetes (PASSÉ)
  ('Avez-vous envisage Nomad comme alternative a Kubernetes ?',                  'Pierre B.',  3, NOW() - INTERVAL '2 hours 30 minutes', 2),
  ('Quel outil de monitoring recommandez-vous avec K8s ?',                       'Anna K.',    9, NOW() - INTERVAL '2 hours 20 minutes', 2),

  -- Session 8 — Motion Design (PASSÉ)
  ('Framer Motion vs GSAP : lequel choisir pour un projet Next.js ?',            'David N.',   7, NOW() - INTERVAL '3 hours 30 minutes', 8),

  -- Session 3 — WebAssembly (FUTUR)
  ('WASM est-il vraiment pret pour des apps de production ?',                    'Claire F.',  4, NOW() - INTERVAL '30 minutes', 3),
  ('Rust ou C++ pour compiler vers WASM ? Recommandations ?',                    'Anonyme',    2, NOW() - INTERVAL '20 minutes', 3);

-- ── Vérification ─────────────────────────────────────────────
SELECT '✅ Seed termine avec succes !' AS status;

SELECT 'admin'           AS "table", COUNT(*) AS "rows" FROM admin
UNION ALL SELECT 'event',            COUNT(*) FROM event
UNION ALL SELECT 'room',             COUNT(*) FROM room
UNION ALL SELECT 'speaker',          COUNT(*) FROM speaker
UNION ALL SELECT 'session',          COUNT(*) FROM session
UNION ALL SELECT 'session_speaker',  COUNT(*) FROM session_speaker
UNION ALL SELECT 'question',         COUNT(*) FROM question;
