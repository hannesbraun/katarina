import random

from danbooru.danbooru_client import DanbooruClient


class YandereClient(DanbooruClient):
    def _get_index_params(self):
        page = random.randint(1, 2500)
        return {"limit": "200", "page": str(page)}

    _title = "Yande.re"
    _index_url = "https://yande.re/post.json"

    def _get_full_post_url(self, post):
        return "https://yande.re/post/show/" + str(post["id"])

    def _get_tags(self, post):
        tags = post["tags"].split()
        if len(tags) > 0:
            return "`" + "`, `".join(tags) + "`"
        else:
            return "*No tags available*"
