INSERT INTO member (created_at, device_fid)
VALUES (NOW(), 'dummy_fid_1'),
       (NOW(), 'dummy_fid_2');

INSERT INTO favorite_content (created_at, member_id, content_id)
VALUES (DATEADD('DAY', -7, NOW()), 1, 1),
       (DATEADD('DAY', -7, NOW()), 1, 2),
       (DATEADD('DAY', -7, NOW()), 1, 3),
       (DATEADD('DAY', -7, NOW()), 1, 4),
       (DATEADD('DAY', -7, NOW()), 1, 5),
       (DATEADD('DAY', -7, NOW()), 2, 1),
       (DATEADD('DAY', -7, NOW()), 2, 2),
       (DATEADD('DAY', -7, NOW()), 2, 3);
