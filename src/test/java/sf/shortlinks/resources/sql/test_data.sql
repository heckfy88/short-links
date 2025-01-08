INSERT INTO link (id, owner_uid, url, short_url, expiration_time, counter, lim)
VALUES
    (uuid_generate_v4(), '7b0f45b4-7ca8-432b-8e23-3554f740eeff', 'https://example.com/1', 'www.short1.ru/1', '02:00:00', 9, 10),
    (uuid_generate_v4(), '15c713b8-4be2-48c2-99ad-d12825992318', 'https://example.com/2', 'www.short1.ru/2', '02:00:00', 0,10),
    (uuid_generate_v4(), uuid_generate_v4(), 'https://example.com/3', 'www.short3.ru/3', '02:00:00', 0,10),
    (uuid_generate_v4(), uuid_generate_v4(), 'https://example.com/4', 'www.short4.ru/4', '02:00:00', 0,10),
    (uuid_generate_v4(), uuid_generate_v4(), 'https://example.com/5', 'www.short5.ru/5', '02:00:00', 0,10),
    (uuid_generate_v4(), uuid_generate_v4(), 'https://example.com/6', 'www.short6.ru/6', '02:00:00', 0,10),
    (uuid_generate_v4(), uuid_generate_v4(), 'https://example.com/7', 'www.short7.ru/7', '02:00:00', 0,10),
    (uuid_generate_v4(), uuid_generate_v4(), 'https://example.com/8', 'www.short8.ru/8', '02:00:00', 0,10),
    (uuid_generate_v4(), uuid_generate_v4(), 'https://example.com/9', 'www.short9.ru/9', '02:00:00', 0,10),
    (uuid_generate_v4(), '7b0f45b4-7ca8-432b-8e23-3554f740ee00', 'https://example.com/10', 'www.short10.ru/10', '02:00:00', 0,10)
on conflict do nothing;
