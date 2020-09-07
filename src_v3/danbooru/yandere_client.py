import random

from danbooru.danbooru_client import DanbooruClient


class YandereClient(DanbooruClient):
    def _get_index_params(self):
        page = random.randint(1, 5505)
        return {"limit": "100", "page": str(page)}

    _title = "Yande.re"
    _index_url = "https://yande.re/post.json"

    def _get_full_post_url(self, post):
        return "https://yande.re/post/show/" + str(post["id"])

    def _get_tag_list(self, post):
        return post["tags"].split()
