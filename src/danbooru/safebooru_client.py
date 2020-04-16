import random

from danbooru.danbooru_client import DanbooruClient


class SafebooruClient(DanbooruClient):
    def _get_index_params(self):
        page = random.randint(1, 14500)
        return {"page": "dapi", "s": "post", "q": "index", "json": "1", "limit": "200", "pid": str(page)}

    _title = "Safebooru"
    _index_url = "https://safebooru.org/index.php"

    def _is_explicit(self, post):
        rating = post["rating"]
        if rating is None or len(rating) <= 0:
            return False

        if rating.lower() == "explicit":
            return True
        else:
            return False

    def _get_image_url(self, post):
        return "https://safebooru.org/images/" + post["directory"] + "/" + post["image"]

    def _get_full_post_url(self, post):
        return "https://safebooru.org/index.php?page=post&s=view&id=" + str(post["id"])

    def _get_score(self, post):
        score = post["score"]
        if score is None:
            return "*No score available*"
        else:
            return score

    def _get_tag_list(self, post):
        return post["tags"].split()
