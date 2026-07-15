User
│
├── owns
│
Document
│
├── has many
│
Operations
│
├── has many
│
Snapshots
│
├── has many
│
Collaborators
│
└── has many
Sessions




User
---------
id
username
email
password
created_at

Document
---------
id
title
owner_id
created_at
updated_at

DocumentOperation
-----------------
id
document_id
user_id
type
position
text
version
timestamp

DocumentSnapshot
----------------
id
document_id
content
version

Collaborator
------------
document_id
user_id
role

DocumentSession
---------------
document_id
user_id
cursor_position