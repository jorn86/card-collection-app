cardcollection.user:
  postgres_user.present:
    - name: cardcollection
    - password: cardcollection
    - superuser: True

cardcollection.tester:
  postgres_user.present:
    - name: tester
    - superuser: True

card-collection:
  postgres_database.present:
    - owner: cardcollection
