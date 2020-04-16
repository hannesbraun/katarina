import random

from danbooru.danbooru_client import DanbooruClient


class E621Client(DanbooruClient):
    def _get_index_params(self):
        page = random.randint(1, 750)
        return {"limit": "200", "page": str(page)}

    _title = "e621"
    _index_url = "https://e621.net/posts.json"

    def _get_index_list(self, raw_index):
        return raw_index["posts"]

    def _get_image_url(self, post):
        return post["file"]["url"]

    def _get_full_post_url(self, post):
        return "https://e621.net/posts/" + str(post["id"])

    def _get_score(self, post):
        return post["score"]["total"]

    def _get_tag_list(self, post):
        tags = post["tags"]["general"]
        tags.extend(post["tags"]["species"])
        tags.extend(post["tags"]["character"])
        tags.extend(post["tags"]["copyright"])
        tags.extend(post["tags"]["artist"])
        return tags
