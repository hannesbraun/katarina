import random

from danbooru.danbooru_client import DanbooruClient


class GelbooruClient(DanbooruClient):
    def _get_index_params(self):
        page = random.randint(1, 200)
        return {"page": "dapi", "s": "post", "q": "index", "json": "1", "limit": "100", "pid": str(page)}

    _title = "Gelbooru"
    _index_url = "https://gelbooru.com/index.php"

    def _get_full_post_url(self, post):
        return "https://gelbooru.com/index.php?page=post&s=view&id=" + str(post["id"])

    def _get_tag_list(self, post):
        return post["tags"].split()
