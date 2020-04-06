import random

from danbooru.danbooru_client import DanbooruClient


class KonachanClient(DanbooruClient):
    def _get_index_params(self):
        page = random.randint(1, 1213)
        return {"limit": "200", "page": str(page)}

    _title = "Konachan"
    _index_url = "https://konachan.com/post.json"

    def _get_full_post_url(self, post):
        return "https://konachan.com/post/show/" + str(post["id"])

    def _get_tags(self, post):
        tags = post["tags"].split()
        if len(tags) > 0:
            return "`" + "`, `".join(tags) + "`"
        else:
            return "*No tags available*"